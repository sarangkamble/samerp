package admin.JcbPocWork;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.General.GenericDAO;
import utility.RequireData;
import utility.SysDate;

/**
 * Servlet implementation class JcbPocDetails
 */
@WebServlet("/JcbPocDetails")
public class JcbPocDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JcbPocDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		GenericDAO dao = new GenericDAO();
		
		String query = "";
		int result=0;

		List details = null;
		
		String jcbPocSelectList = request.getParameter("jcbPocSelectList");
		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		
				
		if (jcbPocSelectList != null) {
			query="SELECT vehicle_details.vehicle_aliasname,customer_master.`custname`,jcbpoc_master.bucket_hr,"
					+ "jcbpoc_master.breaker_hr,jcbpoc_master.deposit,jcbpoc_master.diesel FROM `jcbpoc_master`,"
					+ "customer_master,vehicle_details WHERE "
					+ "jcbpoc_master.status=0 AND jcbpoc_master.intcustid=customer_master.intcustid AND "
					+ "jcbpoc_master.intvehicleid=vehicle_details.vehicle_id AND jcbpoc_master.data='"+jcbPocSelectList+"' ORDER BY jcbpoc_master.intjcbpocid DESC";
			details=dao.getData(query);
			Iterator itr = details.iterator();
			while (itr.hasNext()) {
				out.print(itr.next() + "~");

			}
		}
		if (fromDate != null && toDate !=null) {
			query="SELECT vehicle_details.vehicle_aliasname,customer_master.`custname`,jcbpoc_master.bucket_hr,"
					+ "jcbpoc_master.breaker_hr,jcbpoc_master.deposit,jcbpoc_master.diesel FROM `jcbpoc_master`,"
					+ "customer_master,vehicle_details WHERE "
					+ "jcbpoc_master.status=0 AND jcbpoc_master.intcustid=customer_master.intcustid AND "
					+ "jcbpoc_master.intvehicleid=vehicle_details.vehicle_id AND jcbpoc_master.data='"+jcbPocSelectList+"' ORDER BY jcbpoc_master.intjcbpocid DESC";
			details=dao.getData(query);
			Iterator itr = details.iterator();
			while (itr.hasNext()) {
				out.print(itr.next() + "~");

			}
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		GenericDAO dao = new GenericDAO();
		RequireData rd =new RequireData();
		SysDate sd=new SysDate();
		
		String CustomerSearch = request.getParameter("q");
		String CustomerPrint = request.getParameter("CustomerPrint");
		String updateselect = request.getParameter("updateselect");
		String update = request.getParameter("update");
		String jcbpocid = request.getParameter("jcbpocid");
		String deleteJcbId =request.getParameter("deleteJcbId");
		String CustomerProjectId=request.getParameter("CustomerProjectId");
		String CustomerProjectIdUpdate=request.getParameter("CustomerProjectIdUpdate");
		
		String custid=request.getParameter("custid");
		String project_id=request.getParameter("cust_project");
		String bucketRateCustomer=request.getParameter("bucketRateCustomer");
		String breakerRateCustomer=request.getParameter("breakerRateCustomer");
		String radios = request.getParameter("radios");
		
		String custname=request.getParameter("custname");
		String vehicleid=request.getParameter("vehicle");
		String chalanno=request.getParameter("chalanno");
		String chalandate=request.getParameter("chalandate");
		String bucket_hrs=request.getParameter("bucket_hrs");
		String breaker_hrs=request.getParameter("breaker_hrs");
		String bucket_rate = request.getParameter("bucket_rate");
		String breaker_rate = request.getParameter("breaker_rate");
		String deposit = request.getParameter("deposit");
		String diesel = request.getParameter("diesel");
		

		String query = "";
		int result=0;

		List details = null;
		if (custid != null && vehicleid!= null) {
			
			String[] arrayOfString = chalandate.split("-");
			String jcbpocPayId="null";
			if (deposit != "") {
				
				query="SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA='samerp' AND TABLE_NAME='jcbpoc_payment'";
				details=dao.getData(query);
				jcbpocPayId=details.get(0).toString();
				
				String payBank = request.getParameter("payBank");
				String payCheque =request.getParameter("payCheque");
				
				String payMode="";
				String particular="";
				
				String debtorId ="";
				String contactno="CUST_"+request.getParameter("contactno");
				query="SELECT `id` FROM `debtor_master` WHERE `type`='"+contactno+"'";
				details=dao.getData(query);
				debtorId = details.get(0).toString();
				
				
				if (radios.equals("2")) {
					payMode="CHEQUE";
					particular="Cheque No-"+payCheque;
				}
				if (radios.equals("3")) {
					payMode="TRANSFER";
					payCheque="";
					particular="Transfer";
				}
				
				if (radios.equals("1")) {
					
					payMode="CASH";
					payBank="null";
					payCheque="";
					
					String transactionDate=arrayOfString[2]+"-"+arrayOfString[1]+"-"+arrayOfString[0];
					int debit = 0;
					int credit = Integer.parseInt(deposit);
					
					rd.pCashEntry(transactionDate, debit, credit, debtorId);
					String payAmt="";
					String balance=rd.getTotalRemainingBalance(custid, payAmt, deposit);
					
					query = "INSERT INTO `jcbpoc_payment`(`cust_id`,  `description`, `amount`, `total_balance`, `date`, `pay_mode`, `debtorId`) VALUES "
							+ "("+custid+",'CHALAN DEPOSIT','"+deposit+"','"+balance+"','"+transactionDate+"','"+payMode+"',"+debtorId+")";
					result = dao.executeCommand(query);
				}
				if (radios.equals("2") || radios.equals("3")) {
					
					
					String transactionDate=arrayOfString[2]+"-"+arrayOfString[1]+"-"+arrayOfString[0];
					int debit = 0;
					int credit = Integer.parseInt(deposit);
					debtorId = String.valueOf(rd.getDebtorId(contactno));;
					rd.badEntry(payBank, transactionDate, debit, credit, particular, debtorId);
					
					String payAmt="";
					String balance=rd.getTotalRemainingBalance(custid, payAmt, deposit);
					
					query = "INSERT INTO `jcbpoc_payment`(`cust_id`,  `description`, `amount`, `total_balance`, `date`, `pay_mode`,`bank_id`, `cheque_no`, `debtorId`) VALUES "
							+ "("+custid+",'CHALAN DEPOSIT','"+deposit+"','"+balance+"','"+transactionDate+"','"+payMode+"',"+payBank+",'"+payCheque+"',"+debtorId+")";
					result = dao.executeCommand(query);
				}
			}
			String exp_master_id="null";
			if (!diesel.equals("") && diesel != null ) {
				query="SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA='samerp' AND TABLE_NAME='expenses_master'";
				details=dao.getData(query);
				exp_master_id=details.get(0).toString();
				
				String debtorId ="";
				String contactno="CUST_"+request.getParameter("contactno");
				query="SELECT `id` FROM `debtor_master` WHERE `type`='"+contactno+"'";
				details=dao.getData(query);
				debtorId = details.get(0).toString();
				
				String transactionDate=arrayOfString[2]+"-"+arrayOfString[1]+"-"+arrayOfString[0];
				
				rd.commonExpEntry("2", Integer.parseInt(debtorId), custname, diesel, "CASH", "", "", transactionDate);
			}
		
			query = "INSERT INTO `jcbpoc_master`(`intjcbpocid`, `intcustid`, `project_id`, `intvehicleid`, `chalanno`, `data`, `bucket_hr`, `breaker_hr`, `bucket_rate`, `breaker_rate`, `deposit`, `diesel`, `exp_master_id`,`jcbpoc_payment_id`) VALUES (DEFAULT,'"+custid+"','"+project_id+"','"+vehicleid+"','"+chalanno+"','"+arrayOfString[2]+"-"+arrayOfString[1]+"-"+arrayOfString[0]+"','"+bucket_hrs+"','"+breaker_hrs+"','"+bucket_rate+"','"+breaker_rate+"','"+deposit+"','"+diesel+"',"+exp_master_id+","+jcbpocPayId+")";
			System.out.println(">>"+query);
			result = dao.executeCommand(query);

			if (result == 1) {
				HttpSession session=request.getSession();
		        session.setAttribute("status","Chalan Add Successfully!");
				response.sendRedirect("jsp/admin/jcb-poc-work/jcb-pocDetails.jsp");
			} else {
				out.print("something wrong");
			}
		}
		if (update !=null && jcbpocid != null) {
			
			query="SELECT `deposit` FROM `jcbpoc_master` WHERE `intjcbpocid`="+jcbpocid;
			details=dao.getData(query);
			Iterator itr = details.iterator();
			String updateDeposit="";
			while (itr.hasNext()) {
				updateDeposit=(String) itr.next();

			}
			
			if (!deposit.equals(updateDeposit)) {
				if (updateDeposit.equals("")) {
					updateDeposit="0";
				}
				if (deposit.equals("")) {
					deposit="0";
				}
				int newDeposit=Integer.parseInt(deposit);
				int oldDeposit=Integer.parseInt(updateDeposit);
				
				if (newDeposit != oldDeposit) {
					
					String[] arrayOfString = chalandate.split("-");
					String transactionDate=arrayOfString[2]+"-"+arrayOfString[1]+"-"+arrayOfString[0];
					
					int debit = oldDeposit;
					int credit = 0;
					String debtorId = "1";
					
//					rd.pCashEntry(transactionDate, debit, credit, debtorId);
					
					String contactno="CUST_"+request.getParameter("contactno");
					query="SELECT `id` FROM `debtor_master` WHERE `type`='"+contactno+"'";
					details=dao.getData(query);
					
					debit = 0;
					credit = newDeposit;
					debtorId = details.get(0).toString();
//					rd.pCashEntry(transactionDate, debit, credit, debtorId);
				}
			}
			String[] arrayOfString = chalandate.split("-");
			query = "UPDATE `jcbpoc_master` SET `project_id`="+project_id+",`intvehicleid`='"+vehicleid+"',`chalanno`='"+chalanno+"',`data`='"+arrayOfString[2]+"-"+arrayOfString[1]+"-"+arrayOfString[0]+"',`bucket_hr`='"+bucket_hrs+"',`breaker_hr`='"+breaker_hrs+"',`bucket_rate`='"+bucket_rate+"',`breaker_rate`='"+breaker_rate+"',`deposit`='"+deposit+"',`diesel`='"+diesel+"' WHERE `intjcbpocid`="+jcbpocid;

			result = dao.executeCommand(query);

			if (result == 1) {
				HttpSession session=request.getSession();
		        session.setAttribute("status","Chalan Update Successfully!");
				response.sendRedirect("jsp/admin/jcb-poc-work/jcb-pocDetails.jsp");
			} else {
				out.print("something wrong");
			}
		}
		if (deleteJcbId != null) {
			query="SELECT `intcustid`,`deposit`,`diesel`,jcbpoc_payment.pay_mode,jcbpoc_payment.bank_id,`exp_master_id` FROM `jcbpoc_master`,jcbpoc_payment WHERE `jcbpoc_payment_id`=jcbpoc_payment.id AND `intjcbpocid`="+deleteJcbId;
			details=dao.getData(query);
			Iterator itr = details.iterator();
			
			Object custidproject="";
			String updateDeposit="";
			String updateDiesel="";
			String payMode="";
			Object payBankId="";
			Object expId="";
			while (itr.hasNext()) {
				custidproject=itr.next();
				updateDeposit=(String) itr.next();
				updateDiesel=(String) itr.next();
				payMode=(String) itr.next();
				payBankId=itr.next();
				expId=itr.next();
			}
			String Customer_query="DELETE FROM `jcbpoc_master` WHERE `intjcbpocid`="+deleteJcbId;
			
			int Customer_result = dao.executeCommand(Customer_query);

			if (Customer_result == 1) {
				if (!updateDeposit.equals("")) {
					if (payMode.equals("CASH")) {
						
						int debit = Integer.parseInt(updateDeposit);
						int credit = 0;
						String[] todate= sd.todayDate().split("-");
						String transactionDate=todate[2]+"-"+todate[1]+"-"+todate[0];
						rd.pCashEntry(transactionDate, debit, credit, "1");
						
						deposit="";
						String balance=rd.getTotalRemainingBalance(custidproject.toString(), updateDeposit, deposit);
						
						query = "INSERT INTO `jcbpoc_payment`(`cust_id`,  `description`, `bill_amount`, `total_balance`, `date`, `pay_mode`, `debtorId`) VALUES "
								+ "("+custidproject+",'CHALAN DEPOSIT','"+updateDeposit+"','"+balance+"','"+transactionDate+"','"+payMode+"',1)";
						result = dao.executeCommand(query);
					}
					if (payMode.equals("CHEQUE") || payMode.equals("TRANSFER")) {
						int debit = Integer.parseInt(updateDeposit);
						int credit = 0;
						String[] todate= sd.todayDate().split("-");
						String transactionDate=todate[2]+"-"+todate[1]+"-"+todate[0];
						rd.badEntry(payBankId.toString(), transactionDate, debit, credit, "REVERT", "1");
						
						deposit="";
						String balance=rd.getTotalRemainingBalance(custidproject.toString(), updateDeposit, deposit);
						
						query = "INSERT INTO `jcbpoc_payment`(`cust_id`,  `description`, `bill_amount`, `total_balance`, `date`, `pay_mode`,`bank_id`,`debtorId`) VALUES "
								+ "("+custidproject+",'REVERT','"+updateDeposit+"','"+balance+"','"+transactionDate+"','"+payMode+"',"+payBankId+",1)";
						result = dao.executeCommand(query);
					}
				}
				if (!updateDiesel.equals("")) {
					query="DELETE FROM `expenses_master` WHERE `exp_id`="+expId;
					dao.executeCommand(query);
				}
				HttpSession session=request.getSession();  
		        session.setAttribute("status","Chalan Deleted Successfully!");
		        response.sendRedirect("jsp/admin/jcb-poc-work/jcb-pocDetails.jsp");
			} else {
				HttpSession session=request.getSession();  
		        session.setAttribute("error","2");
//				session.setAttribute("status","Project Not Deleted!");
		        response.sendRedirect("jsp/admin/jcb-poc-work/jcb-pocDetails.jsp");
			}
		}
		if (updateselect != null) {
			query="SELECT customer_master.`custname`,customer_master.address,customer_master.contactno,jcbpoc_master.bucket_rate,jcbpoc_master.breaker_rate,"
					+ "jcbpoc_master.`chalanno`,"
					+ "vehicle_details.vehicle_aliasname,jcbpoc_master.`data`,jcbpoc_master.bucket_hr,jcbpoc_master.breaker_hr,jcbpoc_master.deposit,jcbpoc_master.diesel,jcbpoc_master.intjcbpocid FROM `jcbpoc_master`,"
					+ "customer_master,vehicle_details WHERE jcbpoc_master.intcustid=customer_master.intcustid AND "
					+ "jcbpoc_master.intvehicleid=vehicle_details.vehicle_id AND jcbpoc_master.intjcbpocid="+updateselect;
			details=dao.getData(query);
			Iterator itr = details.iterator();
			while (itr.hasNext()) {
				out.print(itr.next() + "~");

			}
		}
		if (CustomerPrint != null) {
			query="SELECT `intcustid`, `custname`, `address`, `contactno`, `bucket_rate`, `breaker_rate` FROM `customer_master` where `intcustid`="+CustomerPrint;
			details=dao.getData(query);
			Iterator itr = details.iterator();
			while (itr.hasNext()) {
				out.print(itr.next() + "~");

			}
		}
		if (CustomerSearch != null){
			query="SELECT `intcustid`,`custname`,`contactno` FROM `customer_master` WHERE `custname` LIKE '"+CustomerSearch+"%' UNION SELECT `intcustid`,`contactno`,`custname` FROM customer_master WHERE `contactno` LIKE '"+CustomerSearch+"%'";
			details = dao.getData(query);

			Iterator itr = details.iterator();
			while (itr.hasNext()) {
				out.print(itr.next() + ",");

			}
		}
		if (custid !=null && bucketRateCustomer !=null) {
			query = "UPDATE `customer_master` SET `bucket_rate`="+bucketRateCustomer+" WHERE `intcustid`="+custid;

			result = dao.executeCommand(query);

			if (result == 1) {
				out.print("Buket Rate Update Successfully!");
			} else {
				out.print("Something Wrong!");
			}
		}
		if (custid !=null && breakerRateCustomer !=null) {
			query = "UPDATE `customer_master` SET `breaker_rate`="+breakerRateCustomer+" WHERE `intcustid`="+custid;

			result = dao.executeCommand(query);

			if (result == 1) {
				out.print("Breaker Rate Update Successfully!");
			} else {
				out.print("Something Wrong!");
			}
		}
		if (CustomerProjectId != null) {
			query="SELECT `id`, `project_name` FROM `jcbpoc_project` WHERE `status`=0 AND `cust_id`="+CustomerProjectId;
			details = dao.getData(query);

			Iterator itr = details.iterator();
			while (itr.hasNext()) {
				out.print(itr.next() + ",");

			}
		}
		if (CustomerProjectIdUpdate != null) {
			query="SELECT jcbpoc_project.id,jcbpoc_project.project_name FROM `jcbpoc_master`,jcbpoc_project WHERE jcbpoc_master.intcustid=jcbpoc_project.cust_id AND jcbpoc_master.intjcbpocid="+CustomerProjectIdUpdate;
			details = dao.getData(query);

			Iterator itr = details.iterator();
			while (itr.hasNext()) {
				out.print(itr.next() + ",");

			}
		}


	}

}
