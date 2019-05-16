/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sushiparsexmltodb;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author blj0011
 */
public class ReportItem
{
    private int id;
    private String printIssn;
    private String onlineIssn;
    private String itemPlatform;
    private String itemName;
    private String itemDataType;
    private String proprietary;
    private String itemPublisher;
    private Map<String, List<String>> itemPeriod;

    public ReportItem(int id, String printIssn, String onlineIssn, String itemPlatform, String itemName, String itemDataType, String proprietary, String itemPublisher, Map<String, List<String>> itemPeriod)
    {
        this.id = id;
        this.printIssn = printIssn;
        this.onlineIssn = onlineIssn;
        this.itemPlatform = itemPlatform;
        this.itemName = itemName;
        this.itemDataType = itemDataType;
        this.proprietary = proprietary;
        this.itemPublisher = itemPublisher;
        this.itemPeriod = itemPeriod;
    }

    public ReportItem(String printIssn, String onlineIssn, String itemPlatform, String itemName, String itemDataType, String proprietary, String itemPublisher, Map<String, List<String>> itemPeriod)
    {
        id = -1;
        this.printIssn = printIssn;
        this.onlineIssn = onlineIssn;
        this.itemPlatform = itemPlatform;
        this.itemName = itemName;
        this.itemDataType = itemDataType;
        this.proprietary = proprietary;
        this.itemPublisher = itemPublisher;
        this.itemPeriod = itemPeriod;
    }

    public ReportItem()
    {
        id = -1;
        printIssn = "";
        onlineIssn = "";
        itemPlatform = "";
        itemName = "";
        itemDataType = "";
        proprietary = "";
        itemPublisher = "";
        itemPeriod = new HashMap();
    }

    public int getId()
    {
        return id;
    }

    public String getItemDataType()
    {
        return itemDataType;
    }

    public String getItemName()
    {
        return itemName;
    }

    public String getItemPublisher()
    {
        return itemPublisher;
    }

    public String getProprietary()
    {
        return proprietary;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setItemDataType(String itemDataType)
    {
        this.itemDataType = itemDataType;
    }

    public String getPrintIssn()
    {
        return printIssn;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public void setItemPublisher(String itemPublisher)
    {
        this.itemPublisher = itemPublisher;
    }

    public void setPrintIssn(String printIssn)
    {
        this.printIssn = printIssn;
    }

    public String getOnlineIssn()
    {
        return onlineIssn;
    }

    public void setOnlineIssn(String onlineIssn)
    {
        this.onlineIssn = onlineIssn;
    }

    public String getItemPlatform()
    {
        return itemPlatform;
    }

    public void setItemPlatform(String itemPlatform)
    {
        this.itemPlatform = itemPlatform;
    }

    public Map<String, List<String>> getItemPeriod()
    {
        return itemPeriod;
    }

    public void setItemPeriod(Map<String, List<String>> itemPeriod)
    {
        this.itemPeriod = itemPeriod;
    }

    public void setProprietary(String proprietary)
    {
        this.proprietary = proprietary;
    }

    @Override
    public String toString()
    {
        StringBuilder tempPeriod = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : this.itemPeriod.entrySet()) {
            tempPeriod.append(entry.getKey()).append(":::").append(String.join(", ", entry.getValue())).append(" ");
        }
        tempPeriod.setLength(tempPeriod.length());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Print ISSN: ").append(printIssn)
                .append(", Online ISSN: ").append(onlineIssn)
                .append(", Proprietary: ").append(proprietary)
                .append(", Platform: ").append(itemPlatform)
                .append(", Item Publisher: ").append(itemPublisher)
                .append(", Item Name: ").append(itemName)
                .append(", Item Data Type: ").append(itemDataType)
                .append(", Item Period: [").append(tempPeriod.toString()).append("]");

        return stringBuilder.toString();
    }

}
