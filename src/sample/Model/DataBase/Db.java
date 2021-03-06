package sample.Model.DataBase;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IL984626 on 30/12/2017.
 */
public class Db {

    private static int productID=0;

    private static class Holder {
        static Db instance;

        static {
            try {
                instance = new Db();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Db getSingleton() { // Note: "synchronized" not needed
        return Holder.instance;
    }

    public Db() throws Exception {
        String url = "jdbc:sqlite:ev4rent.db";
        Connection conn = SqliteHelper.getConn();
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS Users(ID INTEGER NOT NULL UNIQUE ,UserName TEXT NOT NULL,LasName TEXT NOT NULL,FirstName TEXT NOT NULL,Address TEXT,Phone TEXT,mail TEXT NOT NULL,Role TEXT,Password TEXT,PRIMARY KEY(ID, UserName) );");
        /*stmt.execute("CREATE TABLE IF NOT EXISTS users(email VARCHAR PRIMARY KEY NOT NULL , name VARCHAR NOT NULL,password VARCHAR NOT NULL , age INTEGER NOT NULL ,paypal VARCHAR NOT NULL, hasProducts NUMERIC NOT NULL );");
        stmt.execute("CREATE TABLE IF NOT EXISTS products(id INTEGER PRIMARY KEY, name VARCHAR NOT NULL,price REAL NOT NULL , swap NUMERIC NOT NULL ,donation NUMERIC NOT NULL ,available NUMERIC NOT NULL, rating REAL NOT NULL,category VARCHAR NOT NULL , userEmail VARCHAR NOT NULL ,FOREIGN KEY (userEmail) REFERENCES users(email));");
        stmt.execute("CREATE TABLE IF NOT EXISTS packages(id INTEGER PRIMARY KEY,name VARCHAR NOT NULL ,productId INTEGER  NOT NULL,FOREIGN KEY (productId) REFERENCES products(id));");
        stmt.execute("CREATE TABLE IF NOT EXISTS orders(id INTEGER PRIMARY KEY,productId INTEGER NOT NULL ,renterEmail VARCHAR NOT NULL,tenantEmail VARCHAR NOT NULL,FOREIGN KEY (productId) REFERENCES products(id),FOREIGN KEY (renterEmail) REFERENCES users(email),FOREIGN KEY (tenantEmail) REFERENCES users(email));");
        stmt.execute("CREATE TABLE IF NOT EXISTS swaps(id INTEGER  PRIMARY KEY,productId1 INTEGER NOT NULL,renterEmail1 VARCHAR NOT NULL ,productId2 INTEGER  NOT NULL,renterEmail2 VARCHAR NOT NULL,FOREIGN KEY (renterEmail2) REFERENCES users(email),FOREIGN KEY (renterEmail1) REFERENCES users(email),FOREIGN KEY (productId1) REFERENCES products(id),FOREIGN KEY (productId2) REFERENCES products(id));");*/
    }


    public String getPasswordUser(String username)throws Exception {
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT Password FROM Users WHERE UserName = "+"'"+username+"'"+";";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs=ps.executeQuery();

        return rs.getString("Password");
    }

    public String getRoleUser(String username)throws Exception {
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT Role FROM Users WHERE UserName = "+"'"+username+"'"+";";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs=ps.executeQuery();

        return rs.getString("Role");
    }

    public ObservableList<Courses> getAllCourses() throws Exception {
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT CourseName FROM Courses";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs= ps.executeQuery();
        if (!rs.isBeforeFirst() ) {
            return null;
        }

        List<Courses> listProducts = new ArrayList<Courses>();

        while(rs.next()){
            listProducts.add(new Courses(rs.getString("CourseName")));
        }

        ObservableList<Courses> listOfProducts = FXCollections.observableList(listProducts);

        return listOfProducts;
    }

    public ObservableList<Semester> getAllSemester() throws Exception {
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT Semester,year FROM Semester";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs= ps.executeQuery();
        if (!rs.isBeforeFirst() ) {
            return null;
        }

        List<Semester> listProducts = new ArrayList<Semester>();

        while(rs.next()){
            listProducts.add(new Semester(rs.getString("Semester"),rs.getInt("year")));
        }

        ObservableList<Semester> listOfProducts = FXCollections.observableList(listProducts);

        return listOfProducts;
    }

    public boolean addExam(String Date,int cpsId,String moad){
        Connection conn = null;
        try {
            conn = SqliteHelper.getConn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Statement stmt = conn.createStatement();
            String INSERT_SQL = "INSERT INTO Exams(Date, cpsId,moad) VALUES(?, ?, ?)";
            PreparedStatement ps=conn.prepareStatement(INSERT_SQL);
            ps.setString(1, Date);
            ps.setInt(2, cpsId);
            ps.setString(3, moad);
            ps.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean addUser(String UserName,String LastName,String FirstName,String Address,int Phone,String mail,String Role,String Password){
        Connection conn = null;
        try {
            conn = SqliteHelper.getConn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Statement stmt = conn.createStatement();
            String INSERT_SQL = "INSERT INTO Users(UserName, LastName, FirstName, Address, Phone,mail,Role,Password) VALUES(?, ?, ?, ?, ?, ?,?,?)";
            PreparedStatement ps=conn.prepareStatement(INSERT_SQL);
            ps.setString(1, UserName);
            ps.setString(2, LastName);
            ps.setString(3, FirstName);
            ps.setString(4, Address);
            ps.setInt(5, Phone);
            ps.setString(6, mail);
            ps.setString(7, Role);
            ps.setString(8, Password);
            ps.executeUpdate();
        } catch (SQLException e) {
           return false;
        }
        return true;


    }

    public User getUser(String UserName) throws Exception {
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT * FROM users WHERE UserName LIKE "+"'"+UserName+"'"+";";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs=ps.executeQuery();
        return new User(rs.getString("UserName"),rs.getString("LastName"),rs.getString("FirstName"),rs.getString("Address"),rs.getInt("Phone"),rs.getString("mail"),rs.getString("Role"),rs.getString("Password"));
    }

    public ObservableList<UserGrade> getUserExams(String UserName)throws Exception{
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT TestStudent.Grade,Courses.CourseName,Exams.Moad from Users \n" +
                "Inner Join TestStudent ON TestStudent.StudentID = Users.ID\n" +
                "Inner Join Exams On Exams.ID = TestStudent.ExamID\n" +
                "Inner Join CoursePerSemester On CoursePerSemester.ID = Exams.CpsID\n" +
                "Inner join Courses On Courses.ID = CoursePerSemester.courseID\n" +
                "where \n" +
                "Users.UserName =" + "'"+UserName+"'"+";";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs= ps.executeQuery();
        if (!rs.isBeforeFirst() ) {
            return null;
        }

        List<UserGrade> listProducts = new ArrayList<UserGrade>();

        while(rs.next()){
            listProducts.add(new UserGrade(rs.getInt("Grade"),rs.getString("CourseName"),rs.getString("Moad")));
        }

        ObservableList<UserGrade> listOfProducts = FXCollections.observableList(listProducts);

        return listOfProducts;
    }

    public ObservableList<UserCourse> getUserCourses(String UserName) throws Exception{
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT distinct Courses.CourseName from Users \n" +
                "Inner Join TestStudent On TestStudent.StudentID = Users.ID\n" +
                "Inner Join Exams On Exams.ID = TestStudent.ExamID\n" +
                "Inner Join CoursePerSemester On CoursePerSemester.ID = Exams.CpsID\n" +
                "Inner Join Courses On Courses.ID = CoursePerSemester.courseID\n" +
                "where \n" +
                "Users.UserName =" + "'"+UserName+"'"+";";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs= ps.executeQuery();
        if (!rs.isBeforeFirst() ) {
            return null;
        }

        List<UserCourse> listProducts = new ArrayList<UserCourse>();

        while(rs.next()){
            listProducts.add(new UserCourse(rs.getString("CourseName"))); // pay attention this name of column in the db
        }

        ObservableList<UserCourse> listOfProducts = FXCollections.observableList(listProducts);

        return listOfProducts;
    }

    public boolean checkUser(String userName, String password) throws Exception {
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT * FROM users WHERE UserName LIKE "+"'"+userName+"'"+"AND Password LIKE"+"'"+password+"'"+";";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs=ps.executeQuery();
        if (!rs.isBeforeFirst() ) {
            return false;
        }
        return true;

    }



    public String getUserProduct(int id) throws Exception {
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT * FROM products WHERE id LIKE "+"'"+id+"'"+";";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs=ps.executeQuery();

        return rs.getString("userEmail");
    }

    public void addProduct(String name, Double price ,int donation,int swap,int available,double rating,String category,String userEmail) throws Exception {
        Connection conn = SqliteHelper.getConn();
        String INSERT_SQL = "INSERT INTO products( name, price, donation, swap, available,rating,category,userEmail) VALUES( ?, ?, ?, ?, ?,?,?,?)";
        PreparedStatement ps=conn.prepareStatement(INSERT_SQL);
        //ps.setInt(1,getRowCount("products")+1);
        ps.setString(1, name);
        ps.setDouble(2, price);
        ps.setInt(3, donation);
        ps.setInt(4, swap);
        ps.setInt(5, available);
        ps.setDouble(6, rating);
        ps.setString(7, category);
        ps.setString(8, userEmail);
        ps.executeUpdate();

        //Set Renter hasProducts
        String sql = "UPDATE users SET hasProducts = '1' WHERE email LIKE "+"'"+userEmail+"'"+";";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.executeUpdate();


    }

    public void addOrder(int productId, String renterEmail ,String tenantEmail) throws Exception {
        Connection conn = SqliteHelper.getConn();
        String INSERT_SQL = "INSERT INTO orders(id, productId, renterEmail, tenantEmail) VALUES(?, ?, ?, ?)";
        //add Order to Order Table
        PreparedStatement ps = conn.prepareStatement(INSERT_SQL);
       // ps.setInt(1, getRowCount("orders") + 1);
        ps.setInt(2, productId);
        ps.setString(3, renterEmail);
        ps.setString(4, tenantEmail);
        ps.executeUpdate();

        //set the ordered product as not available
        String sql = "UPDATE products SET available = '1' WHERE id LIKE "+"'"+productId+"'"+";";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.executeUpdate();

    }

    public void addSwap(int productID1,String renterEmail1,int productID2,String renterEmail2) throws Exception {
        Connection conn = SqliteHelper.getConn();
        String INSERT_SQL = "INSERT INTO swaps( productId1, renterEmail1,productId2, renterEmail2) VALUES(?, ?, ?, ?)";
        //add Order to Order Table
        PreparedStatement ps = conn.prepareStatement(INSERT_SQL);
        ps.setInt(1, productID1);
        ps.setString(2, renterEmail1);
        ps.setInt(3, productID2);
        ps.setString(4, renterEmail2);
        ps.executeUpdate();
        //set the ordered product as not available
        String sql1 = "UPDATE products SET available = '1' WHERE id LIKE "+"'"+productID1+"'"+";";
        PreparedStatement pstmt1 = conn.prepareStatement(sql1);
        pstmt1.executeUpdate();
        String sql2 = "UPDATE products SET available = '1' WHERE id LIKE "+"'"+productID2+"'"+";";
        PreparedStatement pstmt2 = conn.prepareStatement(sql2);
        pstmt2.executeUpdate();


    }

    public ObservableList<ProductShow> getAllProducts() throws Exception {
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT id,category,name,price,swap,donation FROM products WHERE available='0'";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs= ps.executeQuery();
        if (!rs.isBeforeFirst() ) {
            return null;
        }
        List<ProductShow> listProducts=new ArrayList<ProductShow>();
        while(rs.next()){
            listProducts.add(new ProductShow(rs.getInt("id"),rs.getString("category"),rs.getString("name"),rs.getDouble("price"),rs.getInt("swap"),rs.getInt("donation")));
        }

        ObservableList<ProductShow> listOfProducts = FXCollections.observableList(listProducts);

        return listOfProducts;
    }

    public ObservableList<ProductShow> getUserProducts(String email) throws Exception {
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT * FROM products WHERE userEmail LIKE "+"'"+email+"'"+";";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs=ps.executeQuery();
        if (!rs.isBeforeFirst() ) {
            return null;
        }
        List<ProductShow> listProducts=new ArrayList<ProductShow>();
        while(rs.next()){
            listProducts.add(new ProductShow(rs.getInt("id"),rs.getString("category"),rs.getString("name"),rs.getDouble("price"),rs.getInt("swap"),rs.getInt("donation")));
        }

        ObservableList<ProductShow> listOfProducts = FXCollections.observableList(listProducts);

        return listOfProducts;
    }

    public ObservableList<ProductShow> getAllProductsPrice(double price) throws Exception{
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT * FROM products WHERE price<"+"'"+price+"'"+";";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs=ps.executeQuery();
        if (!rs.isBeforeFirst() ) {
            return null;
        }
        List<ProductShow> listProducts=new ArrayList<ProductShow>();
        while(rs.next()){
            listProducts.add(new ProductShow(rs.getInt("id"),rs.getString("category"),rs.getString("name"),rs.getDouble("price"),rs.getInt("swap"),rs.getInt("donation")));
        }
        ObservableList<ProductShow> listOfProducts = FXCollections.observableList(listProducts);


        return listOfProducts;
    }

    public ObservableList<Pair<ProductShow,ProductShow>> getAllSwaps(String email) throws Exception {
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT * FROM swaps WHERE renterEmail1 LIKE "+"'"+email+"'"+" OR "+"renterEmail2 LIKE "+"'"+email+"'"+";";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs=ps.executeQuery();
        if (!rs.isBeforeFirst() ) {
            return null;
        }
        List<Pair<ProductShow,ProductShow>> listProducts=new ArrayList<>();
        while(rs.next()){
            String queryProduct1 = "SELECT * FROM products WHERE id LIKE "+"'"+rs.getInt("id")+"'"+";";
            String queryProduct2 = "SELECT * FROM products WHERE id LIKE "+"'"+rs.getInt("id")+"'"+";";
            PreparedStatement ps1=conn.prepareStatement(queryProduct1);
            ResultSet rs1=ps1.executeQuery();
            ProductShow product1=new ProductShow(rs1.getInt("id"),rs1.getString("category"),rs1.getString("name"),rs1.getDouble("price"),rs1.getInt("swap"),rs1.getInt("donation"));
            PreparedStatement ps2=conn.prepareStatement(queryProduct2);
            ResultSet rs2=ps2.executeQuery();
            ProductShow product2=new ProductShow(rs2.getInt("id"),rs2.getString("category"),rs2.getString("name"),rs2.getDouble("price"),rs2.getInt("swap"),rs2.getInt("donation"));
            if(isMyProduct(product1.getId(),email))
                listProducts.add(new Pair<ProductShow,ProductShow>(product1,product2));
            else
                listProducts.add(new Pair<ProductShow,ProductShow>(product2,product1));

        }

        ObservableList<Pair<ProductShow,ProductShow>> listOfProducts = FXCollections.observableList(listProducts);

        return listOfProducts;
    }

    public boolean isMyProduct(int productID,String email) throws Exception {
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT * FROM products WHERE id LIKE "+"'"+productID+"'"+" AND "+"userEmail LIKE "+"'"+email+"'"+";";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs=ps.executeQuery();
        if (!rs.isBeforeFirst() ) {
            return false;
        }
        else
            return true;
    }

    public boolean hasOrders(String email) throws Exception {
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT * FROM orders WHERE tenantEmail LIKE "+"'"+email+"'"+";";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs=ps.executeQuery();
        if (!rs.isBeforeFirst() ) {
            return false;
        }
        else
            return true;
    }

    public boolean hasProducts(String email) throws Exception {
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT * FROM products WHERE userEmail LIKE "+"'"+email+"'"+";";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs=ps.executeQuery();
        if (!rs.isBeforeFirst() ) {
            return false;
        }
        else
            return true;
    }

    public ObservableList<ProductShow> getUserOrders(String email) throws Exception{
        Connection conn = SqliteHelper.getConn();
        String query = "SELECT * FROM orders WHERE tenantEmail LIKE "+"'"+email+"'"+";";
        PreparedStatement ps=conn.prepareStatement(query);
        ResultSet rs=ps.executeQuery();
        if (!rs.isBeforeFirst() ) {
            return null;
        }
        List<ProductShow> orders=new ArrayList<ProductShow>();
        while(rs.next()){
            String queryProduct1 = "SELECT * FROM products WHERE id LIKE "+"'"+rs.getInt("productId")+"'"+";";
            PreparedStatement ps1=conn.prepareStatement(queryProduct1);
            ResultSet rs1=ps1.executeQuery();
            ProductShow product1=new ProductShow(rs1.getInt("id"),rs1.getString("category"),rs1.getString("name"),rs1.getDouble("price"),rs1.getInt("swap"),rs1.getInt("donation"));
            orders.add(product1);
        }
        ObservableList<ProductShow> listOfProducts = FXCollections.observableList(orders);


        return listOfProducts;
    }


}



