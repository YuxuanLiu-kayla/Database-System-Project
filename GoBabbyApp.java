import java.sql.* ;
import java.util.Scanner;
import java.util.Random;
import java.util.Date;

class GoBabbyApp
{
    public static void main ( String [ ] args ) throws SQLException
    {
        // Unique table names.  Either the user supplies a unique identifier as a command line argument, or the program makes one up.
        String tableName = "";
        int sqlCode=0;      // Variable to hold SQLCODE
        String sqlState="00000";  // Variable to hold SQLSTATE

        if ( args.length > 0 )
            tableName += args [ 0 ] ;
        else
          tableName += "exampletbl";

        // Register the driver.  You must register the driver before you can use it.
        try { DriverManager.registerDriver ( new com.ibm.db2.jcc.DB2Driver() ) ; }
        catch (Exception cnfe){ System.out.println("Class not found"); }

        // This is the url you must use for DB2.
        //Note: This url may not valid now ! Check for the correct year and semester and server name.
        String url = "jdbc:db2://winter2022-comp421.cs.mcgill.ca:50000/cs421";

        //REMEMBER to remove your user id and password before submitting your code!!
        String your_userid = null;
        String your_password = null;

        //AS AN ALTERNATIVE, you can just set your password in the shell environment in the Unix (as shown below) and read it from there.
        //$  export SOCSPASSWD=yoursocspasswd 
        if(your_userid == null && (your_userid = System.getenv("SOCSUSER")) == null)
        {
          System.err.println("Error!! do not have a password to connect to the database!");
          System.exit(1);
        }
        if(your_password == null && (your_password = System.getenv("SOCSPASSWD")) == null)
        {
          System.err.println("Error!! do not have a password to connect to the database!");
          System.exit(1);
        }

        // Connet to the database
        Connection con = DriverManager.getConnection (url,your_userid,your_password) ;
        Statement stmt = con.createStatement ( ) ;

        

        // The real code starts here
        while (true) {
            System.out.println();
            Scanner myScn = new Scanner(System.in);
            System.out.print("Please enter your practitioner id [E] to exit: ");
            String pracid = myScn.nextLine();  // Read user input
            // check user input
            if (pracid.equals("E")) {
                stmt.close() ;
                con.close() ;
                System.exit(0);
            }
            // Select all prac id of midwives
            String select_mwid = "SELECT pracid FROM Midwife WHERE pracid = ?";
            PreparedStatement prep_midid = con.prepareStatement(select_mwid);
            prep_midid.setString(1, pracid);
            ResultSet mwids = prep_midid.executeQuery();
            boolean isValid = false;

            if (mwids.next()) {
                isValid = true;
                // enter date
                while (true) {
                    isValid = true;
                    System.out.println();
                    Scanner dateInput = new Scanner(System.in);
                    System.out.print("Please enter the date for appointment list [E] to exit: ");
                    String aptdate = dateInput.nextLine();

                    if (aptdate.equals("E")) {
                        stmt.close() ;
                        con.close() ;
                        System.exit(0);
                    }

                    // extract all appointments for that date for that midwife (order by time)
                    String date_apt = "SELECT p.coupleid, p.numpreg, a.atime, m.name, m.hcardid, a.aptmtid, " +
                                    "CASE WHEN w.pracid = p.ppracid THEN 'P' ELSE 'B' END " +
                                    "FROM Appointment a, Midwife w, Pregnancy p, Couple c, Mother m " +
                                    "WHERE a.adate = ? AND w.pracid = ? AND a.pracid = w.pracid AND " +
                                    "a.coupleid = p.coupleid AND a.numpreg = p.numpreg AND " +
                                    "p.coupleid = c.coupleid AND c.hcardid = m.hcardid " + 
                                    "ORDER BY a.atime";
                    PreparedStatement prep_app_list = con.prepareStatement(date_apt);
                    prep_app_list.setString(1, aptdate);  // set date
                    prep_app_list.setString(2, pracid);  // set prac id
                    ResultSet app_list = prep_app_list.executeQuery();

                    // print out information
                    int app_counter = 0;
                    String app_result = "";

                    String coupleid_str = "";
                    String numpreg_str = "";
                    String aptmtid_str = "";
                    String name_str = "";
                    String hcardid_str = "";
                    while (app_list.next()) {
                        app_counter += 1;
                        app_result += app_counter + ":  " + app_list.getString("atime") + " " + app_list.getString(7) + 
                        " " + app_list.getString("name") + " " + app_list.getString("hcardid") + "\n";
                        coupleid_str += app_list.getString("coupleid") + "&";
                        numpreg_str += app_list.getString("numpreg") + "&";
                        aptmtid_str += app_list.getString("aptmtid") + "&";
                        name_str += app_list.getString("name") + "&";
                        hcardid_str += app_list.getString("hcardid") + "&";
                    }
                    // if no appointment for the input date
                    if (coupleid_str.length() == 0) {
                        System.out.println();
                        System.out.println("No appointments for the given date. \n");
                        continue;
                    }

                    coupleid_str = coupleid_str.substring(0, coupleid_str.length() - 1);
                    numpreg_str = numpreg_str.substring(0, numpreg_str.length() - 1);
                    aptmtid_str = aptmtid_str.substring(0, aptmtid_str.length() - 1);
                    name_str = name_str.substring(0, name_str.length() - 1);
                    hcardid_str = hcardid_str.substring(0, hcardid_str.length() - 1);

                    // split the above strings to array
                    String[] coupleid_arr = coupleid_str.split("&");
                    String[] numpreg_arr = numpreg_str.split("&");
                    String[] aptmtid_arr = aptmtid_str.split("&");
                    String[] name_arr = name_str.split("&");
                    String[] hcardid_arr = hcardid_str.split("&");

                    app_list.close();

                    // enter appt menu
                    while (true) {
                        // display all appointments on the given date
                        System.out.println();
                        System.out.println(app_result);

                        // move cursor back to the front
                        //app_list.beforeFirst();

                        Scanner apt_num_input = new Scanner(System.in);
                        System.out.print("Enter the appointment number that you would like to work on. \n" +
                                  "[E] to exit [D] to go back to another date: ");
                        String apt_num = apt_num_input.nextLine();
                        // if the user types 'E'
                        if (apt_num.equals("E")) {
                            stmt.close() ;
                            con.close() ;
                            System.exit(0);
                        } // if the user types 'D'
                        else if (apt_num.equals("D")) {
                            break;
                        } // if the user types an invalid apointment number
                        else if (Integer.parseInt(apt_num) < 1 || Integer.parseInt(apt_num) > app_counter) {
                            System.out.println();
                            System.out.println("Appointment number out of range. \n");
                            continue;
                        } // if the user types a valid appointment number
                        else if (Integer.parseInt(apt_num) >= 1 && Integer.parseInt(apt_num) <= app_counter) {
                            int selected_aptmt = Integer.parseInt(apt_num);
                            // enter the 5-choice menu
                            while (true) {
                                // move cursor to the given index row
                                //app_list.absolute(Integer.parseInt(apt_num));

                                Scanner choice_input = new Scanner(System.in);

                                System.out.println();
                                System.out.println("For " + name_arr[selected_aptmt - 1] + " " + hcardid_arr[selected_aptmt - 1] + "\n");
                                System.out.println("1. Review notes");
                                System.out.println("2. Review tests");
                                System.out.println("3. Add a note");
                                System.out.println("4. Precribe a test");
                                System.out.println("5. Go back to the appointments  \n");
                                System.out.print("Enter your choice: ");

                                String choice = choice_input.nextLine();

                                // if enter 5: back to lising all appointments
                                if (choice.equals("5")) {
                                    break;
                                } 
                                
                                // if enter 1: review notes
                                else if (choice.equals("1")) {
                                    // list all notes relevant for the pregnancy
                                    String coupleid = coupleid_arr[selected_aptmt - 1];
                                    String numpreg = numpreg_arr[selected_aptmt - 1];
                                    String all_notes_sql = "SELECT CAST(o.odate AS DATE), o.otime, o.content " +
                                                        "FROM Observation o, Appointment a, Pregnancy p " + 
                                                        "WHERE p.coupleid = ? AND p.numpreg = ? AND " + 
                                                        "a.coupleid = p.coupleid AND a.numpreg = p.numpreg AND " +
                                                        "a.aptmtid = o.aptmtid " +
                                                        "ORDER BY o.odate DESC, o.otime DESC";
                                    PreparedStatement notes_stmt = con.prepareStatement(all_notes_sql);
                                    notes_stmt.setString(1, coupleid);
                                    notes_stmt.setInt(2, Integer.parseInt(numpreg));
                                    ResultSet notes_list = notes_stmt.executeQuery();
                                    while (notes_list.next()) {
                                        System.out.println(notes_list.getString(1) + " " + notes_list.getString("otime") +
                                        " " + notes_list.getString("content"));
                                    }
                                    notes_list.close();
                                    System.out.println();
                                    System.out.println("--------------------------------------------");
                                    continue;  // continue to the 5-choice menu
                                } 
                                
                                // if enter 2: review tests
                                else if (choice.equals("2")) {
                                    // list all tests relevant for this pregnancy (only for mother)
                                    String coupleid = coupleid_arr[selected_aptmt - 1];
                                    String numpreg = numpreg_arr[selected_aptmt - 1];
                                    String all_test_sql = "SELECT CAST(t.presdate AS DATE), t.ttype, t.result " +
                                                          "FROM Test t, Pregnancy p " + 
                                                          "WHERE p.coupleid = ? AND p.numpreg = ? AND " +
                                                          "t.coupleid = p.coupleid AND t.numpreg = p.numpreg " + 
                                                          "ORDER BY t.presdate DESC";
                                    PreparedStatement test_stmt = con.prepareStatement(all_test_sql);
                                    test_stmt.setString(1, coupleid);
                                    test_stmt.setInt(2, Integer.parseInt(numpreg));
                                    ResultSet tests_list = test_stmt.executeQuery();
                                    while (tests_list.next()) {
                                        String one_test = tests_list.getString(1) + " [" + tests_list.getString("ttype") + 
                                        "] ";
                                        if (tests_list.getString("result") == null) {
                                            one_test += "PENDING";
                                        }
                                        else {
                                            one_test += tests_list.getString("result");
                                        }
                                        System.out.println(one_test);
                                    }
                                    tests_list.close();
                                    System.out.println();
                                    System.out.println("--------------------------------------------");
                                    continue;  // continue to the 5-choice menu
                                } 
                                
                                // if enter 3: add a note
                                else if (choice.equals("3")) {
                                    // get appointment id
                                    String apt_id =  aptmtid_arr[selected_aptmt - 1];

                                    // let user type in a text
                                    Scanner obs_input = new Scanner(System.in);
                                    System.out.print("Please type your observation: ");
                                    String obs = obs_input.nextLine();

                                    // get all note id 
                                    String all_noteid_sql = "SELECT noteid FROM Observation";
                                    ResultSet all_noteid = stmt.executeQuery(all_noteid_sql);
                                    String all_noteid_str = "";
                                    while (all_noteid.next()) {
                                        all_noteid_str += all_noteid.getString("noteid") + " ";
                                    }

                                    // generate a note id
                                    String new_noteid = "";
                                    String id_char = "0123456789";
                                    StringBuilder id_builder = new StringBuilder("n");
                                    Random rand = new Random();
                                    // build valid note id
                                    while (true) {
                                        while (id_builder.length() < 7) {
                                            int char_index = (int) (rand.nextFloat() * id_char.length());
                                            id_builder.append(id_char.charAt(char_index));
                                        }
                                        new_noteid = id_builder.toString();
                                        if (all_noteid_str.contains(new_noteid)) {
                                            continue;
                                        }
                                        break;
                                    }

                                    java.util.Date utilDate = new java.util.Date();
                                    // get current date
                                    java.sql.Date currentDate = new java.sql.Date(utilDate.getTime());
                                    String curDate_str = currentDate.toString();
                                    String curDateTime = curDate_str + " 00:00:00";

                                    // get current time
                                    java.sql.Time currentTime = new java.sql.Time(utilDate.getTime());
                                    String curTime_str = currentTime.toString();
                                    String curTime = curTime_str.substring(0, 5);
                                    
                                    String insert_obs_sql = "INSERT INTO Observation VALUES (\'" + 
                                                            new_noteid + "\', \'" + curDateTime + "\', \'" +
                                                            curTime + "\', \'" + obs + "\', \'" + apt_id + "\')";
                                    stmt.executeUpdate(insert_obs_sql);
                                    System.out.println("Done! Note added successfully.");

                                    System.out.println();
                                    System.out.println("--------------------------------------------");
                                    continue;  // continue to the 5-choice menu
                                }

                                // if enter 4: prescribe a test
                                else if (choice.equals("4")) {
                                    // let user type in the type of test
                                    Scanner type_input = new Scanner(System.in);
                                    System.out.print("Please enter the type of test: ");
                                    String test_type = type_input.nextLine();

                                    // get coupleid and numpreg
                                    String coupleid = coupleid_arr[selected_aptmt - 1];
                                    String numpreg = numpreg_arr[selected_aptmt - 1];

                                    // get all test id
                                    String all_testid_sql = "SELECT testid FROM Test";
                                    ResultSet all_testid = stmt.executeQuery(all_testid_sql);
                                    String all_testid_str = "";
                                    while (all_testid.next()) {
                                        all_testid_str += all_testid.getString("testid") + " ";
                                    }

                                    // generate a test id
                                    String new_testid = "";
                                    String id_char = "0123456789";
                                    StringBuilder id_builder = new StringBuilder("t");
                                    Random rand = new Random();
                                    // build valid note id
                                    while (true) {
                                        while (id_builder.length() < 7) {
                                            int char_index = (int) (rand.nextFloat() * id_char.length());
                                            id_builder.append(id_char.charAt(char_index));
                                        }
                                        new_testid = id_builder.toString();
                                        if (all_testid_str.contains(new_testid)) {
                                            continue;
                                        }
                                        break;
                                    }

                                    java.util.Date utilDate = new java.util.Date();
                                    // get current date
                                    java.sql.Date currentDate = new java.sql.Date(utilDate.getTime());
                                    String curDate_str = currentDate.toString();
                                    String curDateTime = curDate_str + " 00:00:00";

                                    String insert_test_sql = "INSERT INTO Test VALUES (\'" + new_testid + 
                                                            "\', \'" + test_type + "\', \'" + curDateTime + 
                                                            "\', \'" + curDateTime + "\', NULL, NULL, \'" +
                                                            pracid + "\', \'" + coupleid + "\', \'" + numpreg +
                                                            "\', NULL, NULL)";
                                    stmt.executeUpdate(insert_test_sql);
                                    System.out.println("Done! Test added successfully.");

                                    System.out.println();
                                    System.out.println("--------------------------------------------");
                                    continue;  // continue to the 5-choice menu
                                }
                            }
                            //break;
                        } // other invalid commands
                        else {
                            System.out.println("Invalid input. \n");
                            continue;
                        }
                    }
                    // continue to enter date
                }
            }
            if (!isValid) {
                // if the input prac id is not valid (does not exist in the database so no enter while loop)
                System.out.println("Invalid practitioner ID; No such ID exists. \n");
                continue;  // continue to the next iteration of the outer while loop
            }
            break;
        }

        // Finally but importantly close the statement and connection
        stmt.close() ;
        con.close() ;
    }
}


