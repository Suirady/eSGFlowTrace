package com.eservglobal.soa;

import com.eservglobal.mvc.CenterPanelController;
import oracle.soa.management.facade.ComponentInstance;
import oracle.soa.management.facade.CompositeInstance;
import oracle.soa.management.facade.Fault;
import oracle.soa.management.facade.Locator;
import oracle.soa.management.util.ComponentInstanceFilter;
import oracle.soa.management.util.CompositeInstanceFilter;
import oracle.soa.management.util.FaultFilter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class to store data in a file
 * Normally the persistence is done via database or xml files etc. but for our application
 * we will read and write data to a file
 */

public class ComponentData implements Serializable {

    private String filenameAudit;
    private String filenameFaults;
    private boolean whatFile;
    private List exclusions;
    private String instanceID;
    private Locator loc;
    private static ComponentData componentData;
    private List<CompositeInstance> compositeInstances;


    private ComponentData() {
        instanceID = null;
        loc = null;
        exclusions = new ArrayList();
    }

    void setLoc(Locator loc) {
        this.loc = loc;
    }

    public String getFilename() {
        if (whatFile) {
            return filenameAudit;
        } else {
            return filenameFaults;
        }
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
    }

    /*    this app was not designed for multithreaded environment
    so a simple singleton implementation was chosen instead of class holder(private inner class)*/

    public static ComponentData getInstance() {
        if (componentData == null) {
            componentData = new ComponentData();
        }
        return componentData;
    }

    public void storeAuditTrail() throws Exception {
        File temp = File.createTempFile("auditTrail", ".tmp");
        temp.deleteOnExit();
        filenameAudit = temp.getAbsolutePath();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
            if (loc != null) {
                System.out.println(loc);
                CompositeInstanceFilter compositeInFilter = new CompositeInstanceFilter();
                compositeInFilter.setId(instanceID);
                compositeInFilter.setOrderBy(CompositeInstanceFilter.ORDER_BY_CREATION_DATE_ASC);
                compositeInstances = loc.getCompositeInstances(compositeInFilter);
                bw.write("\t\tAudit trail summary for instanceID: " + instanceID);
                bw.newLine();
                bw.newLine();
                Iterator compositeInstancesIterator = compositeInstances.iterator();
                while (compositeInstancesIterator.hasNext()) {
                    CompositeInstance compositeInstance = (CompositeInstance) compositeInstancesIterator.next();
                    if (!exclusions.contains(compositeInstance.getCompositeDN().getCompositeName())) {
                        bw.write(String.format("%s\n%s\n%s\n%s\n%s\n%s",
                                "Parent composite instance id: " + compositeInstance.getParentId(),
                                "Searched composite name: " + compositeInstance.getCompositeDN().getCompositeName(),
                                "Searched composite instance id: " + compositeInstance.getId(),
                                "Title :" + compositeInstance.getTitle(),
                                "Created date: " + compositeInstance.getCreationDate(),
                                "Domain name: " + compositeInstance.getCompositeDN().getDomainName()));
                        bw.newLine();

                        switch (compositeInstance.getState()) {
                            case CompositeInstance.STATE_COMPLETED_SUCCESSFULLY:
                                bw.write("State : COMPLETED_SUCCESSFULLY");
                                bw.newLine();
                                break;
                            case CompositeInstance.STATE_FAULTED:
                                bw.write("State : STATE_FAULTED");
                                bw.newLine();
                                break;
                            case CompositeInstance.STATE_RUNNING:
                                bw.write("State : STATE_RUNNING");
                                bw.newLine();
                                break;
                            case CompositeInstance.STATE_STALE:
                                bw.write("State : STATE_STALE");
                                bw.newLine();
                                break;
                            case CompositeInstance.STATE_RECOVERY_REQUIRED:
                                bw.write("State : STATE_RECOVERY_REQUIRED");
                                bw.newLine();
                                break;
                            case CompositeInstance.STATE_SUSPENDED:
                                bw.write("State : STATE_SUSPENDED");
                                bw.newLine();
                                break;
                            case CompositeInstance.STATE_TERMINATED_BY_USER:
                                bw.write("State : TERMINATED_BY_USER");
                                bw.newLine();
                                break;
                            case CompositeInstance.STATE_UNKNOWN:
                                bw.write("State : STATE_UNKNOWN");
                                bw.newLine();
                                break;
                            default:
                                bw.write("State : UNDEFINED");
                                bw.newLine();
                        }
                        bw.write("##################################################");
                        bw.newLine();
                        ComponentInstanceFilter filter1 = new ComponentInstanceFilter();
                        filter1.setECID(compositeInstance.getECID());
                        filter1.setOrderBy(ComponentInstanceFilter.ORDER_BY_CREATION_DATE_ASC);
                        List<ComponentInstance> childComponentInstances = compositeInstance.getChildComponentInstances(filter1);
                        Iterator<ComponentInstance> iterator = childComponentInstances.iterator();
                        while (iterator.hasNext()) {
                            ComponentInstance componentInstance = iterator.next();

                            bw.write(String.format("%s\n%s\n%s\n%s\n%s",
                                    "Child Component name :" + componentInstance.getComponentName(),
                                    "Child Component instance id :" + componentInstance.getId(),
                                    "State :" + componentInstance.getNormalizedStateAsString(),
                                    "##################################################",
                                    "Payload" + componentInstance.getAuditTrail()));
                            bw.newLine();
                            bw.write("##################################################");
                            bw.newLine();
                        }
                        bw.write("-----------------------------------------------------------");
                    }

                }
            }
            whatFile = true;
        } finally {
            closeLoc();
        }
    }

    public void storeFaults() throws Exception {
        File temp = File.createTempFile("faultsTrail", "tmp");
        temp.deleteOnExit();
        filenameFaults = temp.getAbsolutePath();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {
            if (loc != null) {
                System.out.println(loc);
                bw.write("\t\tFaults summary for instanceID: " + instanceID);
                bw.newLine();
                bw.newLine();
                FaultFilter faultFilter = new FaultFilter();
                faultFilter.setCompositeInstanceId(instanceID);
                faultFilter.setOrderBy(FaultFilter.ORDER_BY_CREATION_DATE_DESC);
                faultFilter.setPageSize(10);
                List<Fault> list = loc.getFaults(faultFilter);
                Iterator<Fault> faults = list.iterator();
                while (faults.hasNext()) {
                    Fault fault = faults.next();
                    bw.write(String.format("%s\n%s\n%s\n%s\n%s",
                            "Composite instance id: " + fault.getCompositeInstanceId(),
                            "Component name: " + fault.getComponentName(),
                            "Creation date: " + fault.getCreationDate(),
                            "Message: " + fault.getMessage(),
                            "##################################################"));
                    bw.newLine();
                }
                bw.write("##################################################");
                bw.newLine();
                whatFile = false;
            }
        } finally {
            closeLoc();
        }
    }

    public void displaySummary() {
        Path path = Paths.get(getFilename());
        try (Scanner scan = new Scanner(Files.newBufferedReader(path))) {
            scan.useDelimiter("#");
            CenterPanelController.areaTextP.setText(scan.next());
            CenterPanelController.saveFileBtnP.setVisible(true);
            scan.close();
        } catch (IOException e) {
            Logger.getLogger(ComponentData.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void closeLoc() {
        System.out.println("Loc value entering closeLoc(): " + loc.toString());
        if (ComponentData.getInstance().loc != null) {
            ComponentData.getInstance().loc.close();
            System.out.println("Loc value after closeLoc(): " + loc.toString());
            System.out.println("Closed Connection!");
        } else {
            System.out.println("Exception while closing Locator handle");
        }
    }
}