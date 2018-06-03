/*Class: CSE 3330
  Semester: Spring 2016
  Student: Lautenslager, Craig
  ID: 1000873407
  Assignment: Project #4
 */
 
import java.sql.*;
import java.util.Scanner;
import java.lang.Integer;
import java.util.StringTokenizer;

final class project4 {
  final static String user = "cpl3407"; //replace XXX with your NetID
  final static String password = "Apple123"; //replace YYY with your mysql password
  final static String db = "cpl3407"; //replace DB with your NetID -> your database 
  final static String jdbc = "jdbc:mysql://localhost:3306/"+db+"?user="+user+"&password="+password;
    
  public static void main ( String[] args ) throws Exception {
    boolean running = true;
    project4 methods = new project4();
          
    while(running){
      running = methods.menu();
    }

  }
    
  public boolean menu(){
    System.out.println("***Airline Database***\nType number of desired option.\n1. Departures\n2. Arrivals\n3. Exit Database");
    Scanner input = new Scanner(System.in);
    switch(input.nextInt()){
    case 1:
      try{
        departures();
      }
      catch(Exception e){
      }
      return true;
    case 2:
      try{
        arrivals();
      }
      catch(Exception e){
      }
      return true;
    case 3:
      return false;
    default:
      System.out.println("Invalid Option...\n");
    return true;
      }
   }
   
  public void departures() throws Exception{
    String arrTime = new String();
	Boolean flightInst = false;
    Connection con;
    Scanner input = new Scanner(System.in);
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    con = DriverManager.getConnection(jdbc);
    
    System.out.println("***Departure***\nEnter Flight Number:");
    String flno = Integer.toString(input.nextInt());
    System.out.println("Enter Leg Number:");
    String legSeq = Integer.toString(input.nextInt());
    System.out.println("Enter date:\nYear:");
    String year = Integer.toString(input.nextInt());
    System.out.println("Month:");
    String month = Integer.toString(input.nextInt());
    System.out.println("Day:");
    String day = Integer.toString(input.nextInt());
    String date = year+"-"+month+"-"+day;

    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery("select * from FlightLeg Where FLNO = '"+flno+"' AND Seq = "+legSeq);
    if(rs.next() == false){
      System.out.println("Error flight leg doesn't exist\n");
      return;
    }
    
    if( Integer.parseInt(legSeq) == 1){
      
      rs = stmt.executeQuery("select * from FlightLegInstance where FLNO = '"+flno+"' and Seq = "+legSeq+" and FDate = '"+date+"'");
      if(rs.next() == true){
        System.out.println("Error: Flight Instance already exists\n");
        return;
      }
      else{
        flightInst = true;
      }
    }
    else {
      int prev = Integer.parseInt(legSeq) - 1;
      String prevSeq = Integer.toString(prev);
      rs = stmt.executeQuery("select * from FlightLegInstance where FLNO = "+flno+" and seq = "+prevSeq+" and FDate = '"+date+"'");
      if(rs.next() == false){
       System.out.println("Error: No prior leg exists.");
       return;
      }
      arrTime = rs.getString("actArr");
      if(arrTime == null){
        System.out.println("Error: No prior arrival time for Leg "+prevSeq+".\n");
        return;
      }
    }    

    System.out.println("Select Departure Time\nEnter Hour:");
    String hour = Integer.toString(input.nextInt());
    System.out.println("Enter Minutes:");
    String min = Integer.toString(input.nextInt());
    String depTime = hour+":"+min+":00";
    String nothing = null;
    System.out.println("Enter Pilot ID:");
    String pilot = Integer.toString(input.nextInt());
    
    
    rs = stmt.executeQuery("select * from Pilot where ID ="+pilot);
    if(rs.next() == false){
      System.out.println("Error: Pilot Does not exist\n");
      return;
    }
    String arrHour = new String();
    String arrMin = new String();
    if(Integer.parseInt(legSeq) != 1){
      
      StringTokenizer token = new StringTokenizer(arrTime, " :");
      arrHour = token.nextToken();
      arrMin = token.nextToken();
      System.out.println(arrHour);
      System.out.println(arrMin);
      
      if(Integer.parseInt(hour) == Integer.parseInt(arrHour)){
        if(Integer.parseInt(min) <= Integer.parseInt(arrMin)){
          System.out.println("Error: Arrival time later than departure time\n");
          return;
         }
      }
      else if(Integer.parseInt(hour) < Integer.parseInt(arrHour)){
        System.out.println("Error: Arrival time later than departure time\n");
        return;
		
      }
    }
    
	if(flightInst)
      stmt.executeUpdate("insert into FlightInstance values ('"+flno+"','"+date+"')");
  
    stmt.executeUpdate("INSERT INTO FlightLegInstance VALUES ('"+flno+"',"+legSeq+",'"+date+"','"+depTime+"',NULL,"+pilot+")");
    System.out.println("Successful Departure\n");
    
    rs.close();
    stmt.close();
    con.close();    
  }

  public void arrivals() throws Exception{
    Scanner input = new Scanner(System.in);
    Connection con;
    Class.forName("com.mysql.jdbc.Driver").newInstance();
    con = DriverManager.getConnection(jdbc);
    Statement stmt = con.createStatement();

    System.out.println("***Arrival***\nEnter Flight Number:");
    String flno = Integer.toString(input.nextInt());
    System.out.println("Enter Leg Number:");
    String legSeq = Integer.toString(input.nextInt());
    System.out.println("Enter Date\nYear:");
    String year = Integer.toString(input.nextInt());
    System.out.println("Month:");
    String month = Integer.toString(input.nextInt());
    System.out.println("Day:");
    String day = Integer.toString(input.nextInt());
    String date = year+"-"+month+"-"+day;
    
    ResultSet rs = stmt.executeQuery("SELECT * FROM FlightLegInstance WHERE FLNO = "+flno+" and Seq = "+legSeq+" and FDate = '"+date+"'");
    
    if(rs.next() == false){
      System.out.println("Error: Flight Leg Instance does not exist.\n");
      return;
    }
    if(rs.getString("ActArr") != null){
      System.out.println("Error: Flight Leg Instance already has an arrival time.\n");
      return;
    }
    String depTime = rs.getString("ActDept");

    System.out.println("Enter Arrival Time\nHour:");
    String hour = Integer.toString(input.nextInt());
    System.out.println("Minute:");
    String min = Integer.toString(input.nextInt());
    String time = hour+":"+min+":00";
    
    StringTokenizer token = new StringTokenizer(depTime," :");
    String depHour = token.nextToken();
    String depMin = token.nextToken();
    //System.out.println(depHour+":"+depMin+":00");

    if(Integer.parseInt(hour) == Integer.parseInt(depHour))
      if(Integer.parseInt(min) <= Integer.parseInt(depMin)){
        System.out.println("Error: Arrival Time before departure time\n");
        return;
      } 
    if(Integer.parseInt(hour) < Integer.parseInt(depHour)){
      System.out.println("Error: Arrival Time before departure time\n");
      return;
    }

    stmt.executeUpdate("UPDATE FlightLegInstance SET ActArr = '"+time+"' WHERE FLNO = '"+flno+"' and Seq = "+legSeq+" and FDate = '"+date+"'");
    
    System.out.println("Sucessfully set Flight Instance's Arrival Time.\n");
  
    rs.close();
    stmt.close();
    con.close();
  }
}
