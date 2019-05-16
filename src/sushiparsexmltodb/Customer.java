/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sushiparsexmltodb;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author blj0011
 */
public class Customer
{
    private String name;
    private String id;
    private List<ReportItem> reportItems;

    public Customer()
    {
        name = "";
        id = "";
        reportItems = new ArrayList();
    }

    public Customer(String name, String id, List<ReportItem> reportItems)
    {
        this.name = name;
        this.id = id;
        this.reportItems = reportItems;
    }

    public List<ReportItem> getReportItems()
    {
        return reportItems;
    }

    public void setReportItems(List<ReportItem> reportItems)
    {
        this.reportItems = reportItems;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "Customer{" + "name=" + name + ", id=" + id + ", reportItems=" + reportItems + '}';
    }
}
