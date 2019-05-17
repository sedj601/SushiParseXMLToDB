/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sushiparsexmltodb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author blj0011
 */
public class DatabaseHandler
{
    Connection connection;

    public DatabaseHandler()
    {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            //connection.setAutoCommit(false);
            System.out.println("Connected to SQLite Db: database.sqlite3");
        }
        catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public boolean insertReportItem(ReportItem reportItem)
    {
        String sqlQueryString = "INSERT INTO ReportItem (proprietary, print_issn, online_issn, item_platform, item_publisher, item_name, item_data_type) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlQueryString)) {
            pstmt.setString(1, reportItem.getProprietary());
            pstmt.setString(2, reportItem.getPrintIssn());
            pstmt.setString(3, reportItem.getOnlineIssn());
            pstmt.setString(4, reportItem.getItemPlatform());
            pstmt.setString(5, reportItem.getItemPublisher());
            pstmt.setString(6, reportItem.getItemName());
            pstmt.setString(7, reportItem.getItemDataType());

            pstmt.executeUpdate();

            int lastReportItemId = getLastInsertTableId("ReportItem");
            reportItem.getItemPeriod().forEach((key, value) -> {                
                String ftHtml = (Integer.parseInt(value.get(0)) > -1) ? value.get(0) : "0";
                String ftPdf = (Integer.parseInt(value.get(1)) > -1) ? value.get(1) : "0";
                String ftTotal = (Integer.parseInt(value.get(2)) > -1) ? value.get(2) : "0";
                
                insertIntoDateTable(lastReportItemId, key, ftHtml, ftPdf, ftTotal);
            });
        }
        catch (SQLException ex) {
            System.out.println("1: " + ex.toString());
            return false;
        }

        return true;
    }

    public boolean insertBatchReportItems(List<ReportItem> reportItems)
    {

        int count = 1;
        int id = getLastInsertTableId("ReportItem") + 1;
        int batchSize = 10000;
        try {
            connection.setAutoCommit(false);
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO ReportItem (proprietary, print_issn, online_issn, item_platform, item_publisher, item_name, item_data_type) VALUES(?, ?, ?, ?, ?, ?, ?)");
            for (int i = 0; i < reportItems.size(); i++) {
                reportItems.get(i).setId(id++);
                System.out.println(id);
                pstmt.setString(1, reportItems.get(i).getProprietary());
                pstmt.setString(2, reportItems.get(i).getPrintIssn());
                pstmt.setString(3, reportItems.get(i).getOnlineIssn());
                pstmt.setString(4, reportItems.get(i).getItemPlatform());
                pstmt.setString(5, reportItems.get(i).getItemPublisher());
                pstmt.setString(6, reportItems.get(i).getItemName());
                pstmt.setString(7, reportItems.get(i).getItemDataType());

                pstmt.addBatch();

                count++;
                if (count % batchSize == 0) {
                    pstmt.executeBatch();
                    connection.commit();
                }
            }

            if (count % batchSize != 0) {
                pstmt.executeBatch();
                connection.commit();
            }
            pstmt.close();
           
            int count2 = 1;
            PreparedStatement pstmt2 = connection.prepareStatement("INSERT INTO DateTbl (report_item_id, month_year, ft_html, ft_pdf, ft_total) VALUES(?, ?, ?, ?, ?);");

            for (int i = 0; i < reportItems.size(); i++) {
                for (Map.Entry<String, List<String>> period : reportItems.get(i).getItemPeriod().entrySet()) {
                    
                    int lastDateId = getLastInsertTableId("DateTbl");
                    String ftHtml = (Integer.parseInt(period.getValue().get(0)) > -1) ? period.getValue().get(0) : "0";
                    String ftPdf = (Integer.parseInt(period.getValue().get(1)) > -1) ? period.getValue().get(1) : "0";
                    String ftTotal = (Integer.parseInt(period.getValue().get(2)) > -1) ? period.getValue().get(2) : "0";
                    
                    pstmt2.setInt(1, reportItems.get(i).getId());
                    pstmt2.setString(2, period.getKey());
                    pstmt2.setString(3, ftHtml);
                    pstmt2.setString(4, ftPdf);
                    pstmt2.setString(5, ftTotal);
                    
                    pstmt2.addBatch();
                    
                    count2++;
                    if (count2 % batchSize == 0) {
                        pstmt.executeBatch();
                        connection.commit();
                    }
                }
            }
            
            if (count2 % batchSize != 0) {
                pstmt2.executeBatch();
                connection.commit();
            }
            
            pstmt2.close();
            connection.setAutoCommit(true);
        }
        catch (SQLException ex) {
            System.out.println(ex.toString());
            return false;
        }

        return true;
    }

    public List<ReportItem> getReportItem()
    {
        List<ReportItem> reportItems = new ArrayList();

        String sqlQueryString = "SELECT * FROM ReportItem;";

        try (Statement stmt = connection.createStatement();
             ResultSet rset = stmt.executeQuery(sqlQueryString)) {

            while (rset.next()) {
                ReportItem reportItem = new ReportItem();
                reportItem.setId(rset.getInt("id"));
                reportItem.setProprietary(rset.getString("proprietary"));
                reportItem.setPrintIssn(rset.getString("print_issn"));
                reportItem.setOnlineIssn(rset.getString("online_issn"));
                reportItem.setItemPlatform(rset.getString("item_platform"));
                reportItem.setItemPublisher(rset.getString("item_publisher"));
                reportItem.setItemName(rset.getString("item_name"));
                reportItem.setItemDataType(rset.getString("item_data_type"));

                reportItems.add(reportItem);
            }
        }
        catch (SQLException ex) {
            System.out.println("4: " + ex.toString());
        }

        reportItems.forEach((t) -> {
            String sqlQueryString2 = "SELECT DateTbl.month_year, DateTbl.ft_html, DateTbl.ft_pdf, DateTbl.ft_total FROM DateTbl WHERE DateTbl.report_item_id = '" + t.getId() + "';";
            try (Statement stmt = connection.createStatement();
                 ResultSet rset = stmt.executeQuery(sqlQueryString2)) {

                while (rset.next()) {
                    String monthYear = rset.getString("month_year");
                    List<String> tempList = new ArrayList();

                    tempList.add(rset.getString("ft_html"));
                    tempList.add(rset.getString("ft_pdf"));
                    tempList.add(rset.getString("ft_total"));

                    t.getItemPeriod().put(monthYear, tempList);
                }
            }
            catch (SQLException ex) {
                System.out.println("5: " + ex.toString());
            }
        });

        return reportItems;
    }

    public void closeConnection()
    {
        try {
            connection.close();
        }
        catch (SQLException ex) {
            System.out.println("SQLiteDBHandler error 4\n" + ex.toString());
        }
    }

    private int getLastInsertTableId(String tableName)
    {
        String sqlQueryString = "SELECT seq FROM sqlite_sequence WHERE name = '" + tableName + "';";

        int i = -1;

        try (Statement stmt = connection.createStatement();
             ResultSet rset = stmt.executeQuery(sqlQueryString)) {

            while (rset.next()) {
                i = Integer.parseInt(rset.getString("seq"));
            }
        }
        catch (SQLException ex) {
            System.out.println("4: " + ex.toString());
        }

        return i;
    }

    private boolean insertIntoDateTable(int lastId, String monthYear, String ft_html, String ft_pdf, String ft_total)
    {
        String sqlQueryString = "INSERT INTO DateTbl (report_item_id, month_year, ft_html, ft_pdf, ft_total) VALUES(?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlQueryString)) {
            pstmt.setInt(1, lastId);
            pstmt.setString(2, monthYear);
            pstmt.setInt(3, Integer.parseInt(ft_html));
            pstmt.setInt(4, Integer.parseInt(ft_pdf));
            pstmt.setInt(5, Integer.parseInt(ft_total));
            
            pstmt.executeUpdate();
        }
        catch (SQLException ex) {
            System.out.println("2: " + ex.toString());
            return false;
        }

        return true;
    }

//    private boolean insertIntoMetricTable(int lastDateId, String ftHtmlValue, String ftPdfValue, String ftTotalValue)
//    {
//        String sqlQueryString = "INSERT INTO Metric (date_id, ft_html, ft_pdf, ft_total) VALUES(?, ?, ?, ?)";
//
//        try (PreparedStatement pstmt = connection.prepareStatement(sqlQueryString)) {
//            pstmt.setInt(1, lastDateId);
//            pstmt.setString(2, ftHtmlValue);
//            pstmt.setString(3, ftPdfValue);
//            pstmt.setString(4, ftTotalValue);
//
//            pstmt.executeUpdate();
//        }
//        catch (SQLException ex) {
//            System.out.println("3: " + ex.toString());
//            return false;
//        }
//
//        return true;
//    }
}
