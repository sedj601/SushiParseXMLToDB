/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sushiparsexmltodb;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author blj0011
 */
public class XmlHandler
{
    private static final List<String> CONTROL_LIST = new ArrayList();

    public List<Customer> customers = new ArrayList();

    public XmlHandler(String fileName)
    {
        try {
            File xmlFile = new File(fileName);
            if (!xmlFile.exists()) {
                System.out.println("Could not find " + fileName + " file!");
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(xmlFile);
            document.getDocumentElement().normalize();

            //This may not need to be extracted!
            //Get usageDataRange
//            NodeList usageDataRange = document.getElementsByTagName("ns3:UsageDateRange");
//            for (int i = 0; i < usageDataRange.getLength(); i++) {
//                for (int t = 0; t < usageDataRange.item(i).getChildNodes().getLength(); t++) {
//                    if (usageDataRange.item(i).getChildNodes().item(t).getNodeName().equals("ns3:Begin")) {
//                        System.out.println("Begin: " + usageDataRange.item(i).getChildNodes().item(t).getTextContent());
//                    }
//                    else if (usageDataRange.item(i).getChildNodes().item(t).getNodeName().equals("ns3:End")) {
//                        System.out.println("End: " + usageDataRange.item(i).getChildNodes().item(t).getTextContent());
//                    }
//
//                }
//            }

            NodeList customer = document.getElementsByTagName("Customer");
            for (int i = 0; i < customer.getLength(); i++) {
                Customer customer1 = new Customer();
                for (int t = 0; t < customer.item(i).getChildNodes().getLength(); t++) {
                    switch (customer.item(i).getChildNodes().item(t).getNodeName()) {
                        case "Name":
                            customer1.setName(customer.item(i).getChildNodes().item(t).getTextContent());
                            break;
                        case "ID":
                            customer1.setId(customer.item(i).getChildNodes().item(t).getTextContent());
                            break;
                        case "ReportItems":
                            customer1.getReportItems().add(extractNodeInformation(customer.item(i).getChildNodes().item(t)));
                            break;
                        default:
                            break;
                    }
                }

                customers.add(customer1);
            }
        }
        catch (ParserConfigurationException | SAXException | IOException ex) {
            System.out.println(ex.toString());
        }
    }

    public List<Customer> getCustomers()
    {
        return this.customers;
    }

    static private ReportItem extractNodeInformation(Node reportItem)
    {
        ReportItem tempReportedItems = new ReportItem();
        List<String> items = new ArrayList();

        NodeList nodeList = ((Element) reportItem).getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element tempElement = (Element) nodeList.item(i);
                switch (tempElement.getNodeName()) {
                    case "ItemIdentifier":
                        switch (tempElement.getElementsByTagName("Type").item(0).getTextContent()) {
                            case "Print_ISSN":
                                items.add("Print_ISSN");
                                String tempItem = tempElement.getElementsByTagName("Value").item(0).getTextContent() == null ? "" : tempElement.getElementsByTagName("Value").item(0).getTextContent();
                                tempReportedItems.setPrintIssn(tempItem);
                                break;
                            case "Online_ISSN":
                                items.add("Online_ISSN");
                                String tempItem2 = tempElement.getElementsByTagName("Value").item(0).getTextContent() == null ? "" : tempElement.getElementsByTagName("Value").item(0).getTextContent();
                                tempReportedItems.setOnlineIssn(tempItem2);
                                break;
                            case "Proprietary":
                                items.add("Proprietary");
                                String tempItem3 = tempElement.getElementsByTagName("Value").item(0).getTextContent() == null ? "" : tempElement.getElementsByTagName("Value").item(0).getTextContent();
                                tempReportedItems.setProprietary(tempItem3);
                                break;
                        }
                        break;
                    case "ItemPlatform":
                        items.add("ItemPlatform");
                        tempReportedItems.setItemPlatform(tempElement.getTextContent());
                        break;
                    case "ItemPublisher":
                        items.add("ItemPublisher");
                        String tempItem3 = tempElement.getTextContent() == null ? "" : tempElement.getTextContent();
                        tempReportedItems.setItemPublisher(tempItem3);
                    case "ItemName":
                        items.add("ItemName");
                        String tempItem2 = tempElement.getTextContent() == null ? "" : tempElement.getTextContent();
                        tempReportedItems.setItemName(tempItem2);
                        break;
                    case "ItemDataType":
                        items.add("ItemDataType");
                        tempReportedItems.setItemDataType(tempElement.getTextContent());
                        break;
                    case "ItemPerformance":
                        String tempString = "";
                        NodeList itemPerformanceNodeList = tempElement.getChildNodes();
                        for (int t = 0; t < itemPerformanceNodeList.getLength(); t++) {
                            if (itemPerformanceNodeList.item(t).getNodeType() == Node.ELEMENT_NODE) {
                                Element tempItemPerformance = (Element) itemPerformanceNodeList.item(t);
                                switch (tempItemPerformance.getNodeName()) {
                                    case "Period":
                                        tempString = tempItemPerformance.getElementsByTagName("Begin").item(0).getTextContent();
                                        if (!CONTROL_LIST.contains(tempString)) {
                                            CONTROL_LIST.add(tempString);
                                        }
                                        break;
                                    case "Instance":
                                        Map<String, List<String>> tempMap = tempReportedItems.getItemPeriod();
                                        if (tempMap.containsKey(getMonthAndYear(tempString))) {
                                            List<String> tempList = tempMap.get(getMonthAndYear(tempString));
                                            tempList.add(tempItemPerformance.getElementsByTagName("Count").item(0).getTextContent());
                                        }
                                        else {
                                            List<String> tempList = new ArrayList();
                                            tempList.add(tempItemPerformance.getElementsByTagName("Count").item(0).getTextContent());
                                            tempMap.put(getMonthAndYear(tempString), tempList);
                                        }
                                        break;
                                }
                            }
                        }

                        break;
                }
            }
        }

        if (!items.contains("Print_ISSN")) {
            tempReportedItems.setPrintIssn("");
        }
        if (!items.contains("Online_ISSN")) {
            tempReportedItems.setOnlineIssn("");
        }
        if (!items.contains("ItemPlatform")) {
            tempReportedItems.setItemPlatform("");
        }
        if (!items.contains("ItemName")) {
            tempReportedItems.setItemName("");
        }
        if (!items.contains("ItemDataType")) {
            tempReportedItems.setItemDataType("");
        }
        if (!items.contains("Proprietary")) {
            tempReportedItems.setProprietary("");
        }
        if (!items.contains("ItemPublisher")) {
            tempReportedItems.setItemPublisher("");
        }

        return tempReportedItems;
    }

    static private String getMonthAndYear(String date)
    {
        DateTimeFormatter tempFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate tempLocalDate = LocalDate.parse(date, tempFormatter);

        return tempLocalDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + " " + tempLocalDate.getYear();
    }
}
