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
                insertIntoDateTable(lastReportItemId, key);
                int lastDateId = getLastInsertTableId("DateTbl");
                String ftHtml = (Integer.parseInt(value.get(0)) > -1) ? value.get(0) : "0";
                String ftPdf = (Integer.parseInt(value.get(1)) > -1) ? value.get(1) : "0";
                String ftTotal = (Integer.parseInt(value.get(2)) > -1) ? value.get(2) : "0";

                insertIntoMetricTable(lastDateId, ftHtml, ftPdf, ftTotal);
            });
        }
        catch (SQLException ex) {
            System.out.println("1: " + ex.toString());
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
            String sqlQueryString2 = "SELECT DateTbl.month_year, Metric.ft_html, Metric.ft_pdf, Metric.ft_total FROM DateTbl INNER JOIN Metric ON DateTbl.id = Metric.date_id WHERE DateTbl.id = '" + t.getId() + "';";
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

    private boolean insertIntoDateTable(int lastId, String monthYear)
    {
        String sqlQueryString = "INSERT INTO DateTbl (report_item_id, month_year) VALUES(?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlQueryString)) {
            pstmt.setInt(1, lastId);
            pstmt.setString(2, monthYear);

            pstmt.executeUpdate();
        }
        catch (SQLException ex) {
            System.out.println("2: " + ex.toString());
            return false;
        }

        return true;
    }

    private boolean insertIntoMetricTable(int lastDateId, String ftHtmlValue, String ftPdfValue, String ftTotalValue)
    {
        String sqlQueryString = "INSERT INTO Metric (date_id, ft_html, ft_pdf, ft_total) VALUES(?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sqlQueryString)) {
            pstmt.setInt(1, lastDateId);
            pstmt.setString(2, ftHtmlValue);
            pstmt.setString(3, ftPdfValue);
            pstmt.setString(4, ftTotalValue);

            pstmt.executeUpdate();
        }
        catch (SQLException ex) {
            System.out.println("3: " + ex.toString());
            return false;
        }

        return true;
    }
}
