/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sushiparsexmltodb;

import java.util.List;

/**
 *
 * @author blj0011
 */
public class SushiParseXMLToDB
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // TODO code application logic here
        XmlHandler xmlHandler = new XmlHandler("untXMLtest.xml");
        List<Customer> customers = xmlHandler.getCustomers();
        customers.forEach(customer -> {
            System.out.println("Customer Name:" + customer.getName());
            System.out.println("Customer ID: " + customer.getId());
            System.out.println("Report Items: ");
            customer.getReportItems().forEach((reportItem) -> {
                System.out.println("\tProprietary: " + reportItem.getProprietary());
                System.out.println("\tPrint ISSN: " + reportItem.getPrintIssn());
                System.out.println("\tOnline ISSN: " + reportItem.getOnlineIssn());
                System.out.println("\tItem Platform: " + reportItem.getItemPlatform());
                System.out.println("\tItem Publisher: " + reportItem.getItemPublisher());
                System.out.println("\tItem Name: " + reportItem.getItemName());
                System.out.println("\tItem Data Type: " + reportItem.getItemDataType());
                System.out.println("\tDate: ");
                reportItem.getItemPeriod().forEach((key, value) -> {
                    System.out.println("\t\t " + key + ": " + String.join(", ", value));
                });
                System.out.println("");
            });
        });

    }

}
