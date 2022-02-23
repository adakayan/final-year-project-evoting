package eVote;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
public class DAO{
	Connection conn;
	Statement stmt;
	PreparedStatement ps;
	ResultSet rs;
	void connect () throws Exception{
		Class.forName("org.postgresql.Driver");
		conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/evmmaster", "postgres", "ayaningram");
	}
	public boolean login(String un, String pw,String Usertype) throws Exception{
		connect();
		stmt = conn.createStatement();
		 rs= stmt.executeQuery("select * from evs_tbl_user_credentials where userid='"+ un +"' and password='"+ pw+ "'");
		if(rs.next()){
			if(rs.getString("usertype").equals(Usertype))
			return true;
		}
		 return false;
	}
	public boolean edit(String Username,String Firstname,String Lastname,String Dateofbirth,String Gender,String Street,String Location,String City,String State,String Pincode,String Mobileno,String Emailid) throws Exception{
		connect();
		ps = conn.prepareStatement("update evs_tbl_user_profile set Firstname = ?, Lastname = ?, Dateofbirth = ?,Gender=?,Street=?,Location=?,City=?,State=?,Pincode=?,Mobileno=?,Emailid=? where Userid = ?");
		ps.setString(1, Firstname);
		ps.setString(2, Lastname);
		ps.setString(3, Dateofbirth);
		ps.setString(4, Gender);
		ps.setString(5, Street);
		ps.setString(6, Location);
		ps.setString(7, City);
		ps.setString(8, State);
		ps.setString(9, Pincode);
		ps.setString(10, Mobileno);
		ps.setString(11, Emailid);
		ps.setInt(12, Integer.parseInt(Username));
		int r=ps.executeUpdate();
		if(r==1)
		return true;
			return false;
	}
	public boolean changepw(String Userid,String Password,String pw) throws Exception{
	connect ();
	 ps = conn.prepareStatement("update evs_tbl_user_credentials SET password=? where userid = ? and password = ?");
	 ps.setString (1, pw);
	 ps.setInt(2, Integer.parseInt(Userid));
	 ps.setString (3, Password);
	int r=ps.executeUpdate();
	if(r==1) {
	return true;
	}
	else
	    return false;
	 }
	public ResultSet fetch(int un) throws Exception{
		connect();
		String query="select * from evs_tbl_user_profile where userid="+un;
		 stmt = conn.createStatement();
		 rs= stmt.executeQuery(query);
		return rs;
	}
	public boolean addparty(String Name,String Leader) throws Exception{
	connect();
	ps = conn.prepareStatement("update EVS_TBL_Party set Approve=1 where Name=? and Leader=?");
	ps.setString (1, Name);
	ps.setString (2, Leader);
	ps.executeUpdate();
	return true;
	}
	public String usr_register (String Firstname,String Lastname,String Dateofbirth,String Gender,String Street,String Location,String City,String State,String Pincode,String Mobileno,String Emailid,String Password) throws Exception {
	connect ();
		 	ps = conn.prepareStatement("insert into evs_tbl_user_credentials(Password,Usertype) values(?,?)");
			ps.setString (1, Password);
			ps.setString (2, "U");
			ps.executeUpdate();
			ps = conn.prepareStatement("Select Userid from evs_tbl_user_credentials where Usertype='U' order by Userid desc");
			rs=ps.executeQuery();
			rs.next();
		ps = conn.prepareStatement("insert into EVS_TBL_User_Profile values(?,?,?,?,?,?,?,?,?,?,?,?)");//for Registering
		ps.setInt (1,rs.getInt("Userid"));
		ps.setString (2,Firstname);
		ps.setString (3,Lastname);
		ps.setString (4,Dateofbirth);
		ps.setString (5,Gender);
		ps.setString (6,Street);
		ps.setString (7,Location);
		ps.setString (8,City);
		ps.setString (9,State);
		ps.setString (10,Pincode);
		ps.setString (11,Mobileno);
		ps.setString (12,Emailid);
		 int r=ps.executeUpdate();
		 if(r==1)
		 return rs.getString("Userid");
		 else return "null";
	}
	public void Approve(String Userid,String Constituency) throws Exception{
	connect ();
	int voter=0;
	String vid="Ind"+Userid;
	ps=conn.prepareStatement("update EVS_TBL_Application set Voted=0,VoterId=?,Approvedstatus=1 where Userid=? and Constituency=?");
	ps.setString (1, vid);
	ps.setString (2, Userid);
	ps.setString (3, Constituency);
	ps.executeUpdate();	
	ps = conn.prepareStatement("select Constituency from EVS_TBL_Election where Constituency=?");
	ps.setString (1,Constituency);
	rs=ps.executeQuery();
	if(rs.next()!=true){
	ps = conn.prepareStatement("insert into EVS_TBL_Election(electiondate,constituency,countingdate,votercount) values('null',?,'null',1)");
	ps.setString(1,Constituency);
	}
	else{
		ps=conn.prepareStatement("select votercount from evs_tbl_election where constituency=?");
		ps.setString(1,Constituency);
		ResultSet res=ps.executeQuery();
		if(res.next())
		 voter=res.getInt("votercount");
		voter=voter+1;
	ps = conn.prepareStatement("update EVS_TBL_Election set Votercount=? where Constituency=?");
	ps.setInt (1,voter);
	ps.setString(2,Constituency);
	}
	ps.executeUpdate();	
	}
	public boolean caste(String Constituency,String Party,String Userid) throws Exception{
	connect();
	ps = conn.prepareStatement("select Voted,VoterId from EVS_TBL_Application where Userid=?");
	ps.setString (1, Userid);
	rs = ps.executeQuery();
	if(rs.next()){
		if((rs.getInt("Voted")==0)&&(rs.getString("VoterId").equals("null")!=true)){
		ps = conn.prepareStatement("update EVS_TBL_Result set Votecount=Votecount+1 where Constituency=? and Partyname=?");
		ps.setString (1, Constituency);
		ps.setString (2, Party);
		ps.executeUpdate();
		ps = conn.prepareStatement("update EVS_TBL_Application set Voted=1 where Userid=?");
		ps.setString (1, Userid);
		ps.executeUpdate();
		return true;
	}}
	return false;
	}
	public String party_register (String Name,String Leader,String Symbol,String Aboutus,String email2,String phone2) throws Exception {
		connect ();
		ps = conn.prepareStatement("insert into EVS_TBL_Party(name,leader,symbol,email,phone,aboutus) values(?,?,?,?,?,?)");//for Registering
		ps.setString (1,Name );
		ps.setString (2,Leader);
		ps.setString (3,Symbol);
		ps.setString (4,email2);
		ps.setString (5,phone2);
		ps.setString (6,Aboutus);
		int r=ps.executeUpdate();
		 if(r==1){
		ps = conn.prepareStatement("Select PartyId from EVS_TBL_Party order by PartyId desc");
			rs=ps.executeQuery();
			rs.next();
		return rs.getString("PartyId");
		 }
		 else return "null";
	}
	public boolean addschedule(String Electiondate,String Constituency,String Countingdate) throws Exception{
	connect();
	ps = conn.prepareStatement("Insert into evs_tbl_election(electiondate,constituency,countingdate) values(?,?,?)");
	ps.setString (1, Electiondate);
	ps.setString (2, Constituency);
	ps.setString (3, Countingdate);
	int r = ps.executeUpdate();
	if(r==1)
	return true;
	return false;
	}
	public String can_register (String Name,String Partyname,String District,String Constituency,String Dateofbirth,String Mobileno ,String Address,String Emailid) throws Exception {
		connect ();
		ps = conn.prepareStatement("insert into EVS_TBL_Candidate(name,partyname,district,constituency,dateofbirth,mobileno,address,emailid) values(?,?,?,?,?,?,?,?)");
		ps.setString (1,Name );
		ps.setString (2,Partyname);
		ps.setString (3,District);
		ps.setString (4,Constituency);
		ps.setString (5,Dateofbirth );
		ps.setString (6,Mobileno);
		ps.setString (7,Address);
		ps.setString (8,Emailid);
		int r= ps.executeUpdate();
		if(r==1){
		ps = conn.prepareStatement("Select CandidateId from EVS_TBL_Candidate order by CandidateId desc");
			rs=ps.executeQuery();
			rs.next();
		return rs.getString("CandidateId");
		}
		else return "null";
	}
	public boolean req_voterid(String Userid,String Password,String Constituency,String dobd,String adpd,String photo) throws Exception{
	connect ();
	ps = conn.prepareStatement("select * from EVS_TBL_User_Credentials where Userid = ? and Password = ?");
	ps.setInt (1, Integer.parseInt(Userid));
	ps.setString (2, Password);
	rs = ps.executeQuery();
	if (rs.next ()){
	ps = conn.prepareStatement("insert into EVS_TBL_Application values(?,?,?,?,0,'null',null,?)");
	ps.setString (1, Userid);
	ps.setString (2, Constituency);
	ps.setString (3, dobd);
	ps.setString (4, adpd);
	ps.setString (5, photo);
	ps.executeUpdate();
	 return true;
	 }
	else return false;
	}
	public ResultSet viewcandidates(String clas) throws Exception{
	connect();
	if(clas.equals("a")){
	 ps = conn.prepareStatement("select Name,Partyname,Constituency,Approve from EVS_TBL_Candidate where Approve=1");
	}
	else{
	 ps = conn.prepareStatement("select Name,Partyname,Constituency,Approve from EVS_TBL_Candidate");
	}
	rs = ps.executeQuery();
	return rs;
	}
	public ResultSet viewschedule(String clas) throws Exception{
	connect();
	if(clas.equals("a")){
	ps = conn.prepareStatement("select * from EVS_TBL_Election where Electiondate!='null'");
	}
	else{
		ps = conn.prepareStatement("select * from EVS_TBL_Election");
	}
	rs = ps.executeQuery();
	return rs;
	}
	public boolean addcandidates(String Partyname,String Constituency,String Name) throws Exception{
	connect();
	String sym=symbol(Partyname);
	if(sym!=null){
	ps = conn.prepareStatement("update EVS_TBL_Candidate set Approve=1 where Constituency=? and Partyname=?  and name=?");
	ps.setString (1, Constituency);
	ps.setString (2, Partyname);
	ps.setString (3, Name);
	int r = ps.executeUpdate();
	if(r==1){
	ps = conn.prepareStatement("insert into EVS_TBL_Result(Constituency,votecount,partyname,candidatename) values(?,0,?,?)");
	ps.setString (1,Constituency);
	ps.setString (2,Partyname);
	ps.setString (3,Name);
	ps.executeUpdate();	
	ps = conn.prepareStatement("select Constituency from EVS_TBL_Election where Constituency=?");
	ps.setString (1,Constituency);
	rs=ps.executeQuery();
	if(rs.next()!=true){
	ps = conn.prepareStatement("insert into EVS_TBL_Election(electiondate,constituency,countingdate,votercount) values('null',?,'null',0)");
	ps.setString (1,Constituency);
	ps.executeUpdate();	
	return true;
	}}}
	return false;
	}
	public ResultSet pending(String Constituency,String edate) throws Exception{
	connect();
	ResultSet res=null;
		ps = conn.prepareStatement("select * from evs_tbl_election where Constituency=?");
		ps.setString (1, Constituency);
		rs = ps.executeQuery();
		while(rs.next()){
		if(rs.getString("electiondate").equals(edate)){
		ps = conn.prepareStatement("select Partyname,CandidateName from evs_tbl_result where constituency=?");
		ps.setString (1, Constituency);
		res = ps.executeQuery();
		break;
		}
		}
		return res;
	}
	public boolean pending2(String Constituency,String cdate) throws Exception{
	connect();
		ps = conn.prepareStatement("select Countingdate from EVS_TBL_Election where Constituency=?");
		ps.setString (1, Constituency);
		rs = ps.executeQuery();
		if(rs.next()){
		if(rs.getString("Countingdate").equals(cdate)){
		return true;
		}}
		return false;
	}
	public ResultSet pending3(String Party) throws Exception{
		connect();
		ps = conn.prepareStatement("select Constituency,CandidateName from EVS_TBL_Result where Partyname=?");
		ps.setString (1, Party);
		rs = ps.executeQuery();
		return rs;
	}
	public String symbol(String Party) throws Exception{
		connect();
 		ps = conn.prepareStatement("select Symbol from EVS_TBL_Party where NAME=? and Approve=1");
 		ps.setString (1, Party);
 		rs = ps.executeQuery();
 		if(rs.next()){
 		return rs.getString("Symbol");
 		}
 		return null;
	}
	public ResultSet viewparty(String clas) throws Exception{
		connect();
	if(clas.equals("a")){
	ps = conn.prepareStatement("select Name,Leader,Symbol,Aboutus,Approve from EVS_TBL_Party where Approve=1");
	}
	else {
		ps = conn.prepareStatement("select Name,Leader,Symbol,Aboutus,Approve from EVS_TBL_Party");
	}
	rs = ps.executeQuery();
		return rs;
	}
	public ResultSet voterrequest() throws Exception{
	connect ();
	ps=conn.prepareStatement("select * from EVS_TBL_Application where approvedstatus=0");
	rs = ps.executeQuery();
	return rs;
	}
	public ResultSet viewvoterid(String Userid) throws Exception{
	connect ();
	ps = conn.prepareStatement("select Constituency,Approvedstatus,VoterId,photo from EVS_TBL_Application where Userid = ?");
	ps.setString (1, Userid);
	rs = ps.executeQuery();
	return rs;	
    }	
	public ResultSet getConstituency() throws Exception{
	connect();
	ps = conn.prepareStatement("select Constituency,Votercount from EVS_TBL_Election");
	rs = ps.executeQuery();
	return rs;
	}
	public ResultSet getParty() throws Exception{
	connect();
	ps = conn.prepareStatement("select distinct Partyname from EVS_TBL_Result");
	rs = ps.executeQuery();
	return rs;
	}
	public ResultSet viewresult(String Constituency) throws Exception{
    connect();
	ps = conn.prepareStatement("select * from EVS_TBL_Result where Constituency=?");
	ps.setString (1, Constituency);
	rs = ps.executeQuery();
	return rs;
	}
	public ResultSet view(String Constituency) throws Exception{
	connect();
	ps = conn.prepareStatement("select Partyname,Votecount from EVS_TBL_Result where Votecount=(select max(Votecount) from EVS_TBL_Result where Constituency=?) and Constituency=?");
	ps.setString (1, Constituency);
	ps.setString (2, Constituency);
	rs = ps.executeQuery();
	return rs;
	}
	public String approval(String clas,String id) throws Exception{
	connect();
	if(clas.equals("c")==true){
	ps = conn.prepareStatement("select Approve from EVS_TBL_Candidate where CandidateId=?");
	}
	else{
	ps = conn.prepareStatement("select Approve from EVS_TBL_Party where PartyId=?");
	}
	ps.setInt (1,Integer.parseInt(id));
	rs = ps.executeQuery();
	if(rs.next()){
		if(rs.getInt("Approve")==1){
			return "Approved";
			}
	else if(rs.getInt("Approve")==0){
		return "In Progress";
	}}
	return "Invalid";
	}
	public int deleteelection(int id) throws Exception
	{
		int status=0;
		connect();
		try {
		ps = conn.prepareStatement("Delete from evs_tbl_election where electionid=?");
		ps.setInt(1, id);
			status=ps.executeUpdate();
		}catch(Exception e){System.out.println(e);}

		return status;
	}
	public int editelection(int id,String edate,String cdate,String cons) throws Exception
	{
		int status=0;
		connect();
		try {
		ps = conn.prepareStatement("Update evs_tbl_election set electiondate=?,countingdate=?,constituency=? where electionid=?");
		ps.setString(1,edate);
		ps.setString(2,cdate);
		ps.setString(3,cons);
		ps.setInt(4, id);
			status=ps.executeUpdate();
		}catch(Exception e){System.out.println(e);}

		return status;
	}
	public boolean deactivecandidates(String Partyname,String Constituency,String Name) throws Exception{
		connect();
		ps = conn.prepareStatement("update EVS_TBL_Candidate set Approve=0 where Constituency=? and Partyname=? and name=?");
		ps.setString (1, Constituency);
		ps.setString (2, Partyname);
		ps.setString (3, Name);
		int r = ps.executeUpdate();
		ps=conn.prepareStatement("delete from evs_tbl_result where Constituency=? and partyname=? and candidatename=?" );
		ps.setString (1, Constituency);
		ps.setString (2, Partyname);
		ps.setString (3, Name);
		int r2= ps.executeUpdate();
		if(r==1 && r2==1)
			return true;
		else
			return false;
	}
}
