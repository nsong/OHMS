package com.vmware.vrack.hms.task.oob.ipmi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import com.vmware.vrack.hms.boardservice.BoardServiceProvider;
import com.vmware.vrack.hms.common.boardvendorservice.api.IBoardService;
import com.vmware.vrack.hms.common.exception.HmsException;
import com.vmware.vrack.hms.common.notification.TaskResponse;
import com.vmware.vrack.hms.common.servernodes.api.ServerNode;
import com.vmware.vrack.hp.ilo.boardservice.BoardService_iLO;
import com.vmware.vrack.intel.rmm.boardservice.BoardService_S2600GZ;

/**
* Test of SelfTestTask methods
* @author Yagnesh Chawda
*
*/
@Ignore
public class SelfTestTaskTest
{
	private static Logger logger = Logger.getLogger(SelfTestTaskTest.class);
	
	/**
	 * Test Case when Host is Valid, But there is No Board Service available
	 * @throws Exception
	 */
	@Test(expected = HmsException.class)
	public void test_executeTask_ValidHost_NoBoardMapping() throws Exception
	{
		logger.info("TS: SelfTestTaskTest: test_executeTask_ValidHost_NoBoardMapping");
		TaskResponse taskResponse = new TaskResponse(new ServerNode("N1", "10.28.197.202", "root", "root123"));
		SelfTestTask selfTestTask = new SelfTestTask(taskResponse);
		
		/*IBoardService boardService = new BoardService_S2600GZ();
		BoardServiceProvider.addBoardService(taskResponse.getNode().getServiceObject(), boardService, true);*/
		selfTestTask.executeTask();
		
	}
	
	/**
	 * Test Case when Host is Invalid, But there is Board Service for the node is available
	 * @throws Exception
	 */
	@Test(expected = HmsException.class)
	public void test_executeTask_InvalidHost_ValidBoardMapping() throws Exception
	{
		logger.info("TS: SelfTestTaskTest: test_executeTask_InvalidHost_ValidBoardMapping");
		TaskResponse taskResponse = new TaskResponse(new ServerNode("N1", "10.28.197.202", "root", "root123"));
		SelfTestTask selfTestTask = new SelfTestTask(taskResponse);
		
		IBoardService boardService = new BoardService_S2600GZ();
		BoardServiceProvider.addBoardService(taskResponse.getNode().getServiceObject(), boardService, true);
		selfTestTask.executeTask();
				
	}
	
	/**
	 * Test Case when Host is Invalid(with invalid OOB Credentials)
	 * @throws Exception
	 */
	@Test(expected = HmsException.class)
	public void test_executeTask_InvalidHostCreds() throws Exception
	{
		logger.info("TS: SelfTestTaskTest: test_executeTask_InvalidHostCreds");
		TaskResponse taskResponse = new TaskResponse(new ServerNode("N1", "10.28.197.202", "root", "root123"));
		SelfTestTask selfTestTask = new SelfTestTask(taskResponse);
		
		IBoardService boardService = new BoardService_S2600GZ();
		BoardServiceProvider.addBoardService(taskResponse.getNode().getServiceObject(), boardService, true);
		selfTestTask.executeTask();
				
	}
	
	/**
	 * Test Case when Host is Valid, and Board Mapping is also Valid
	 * @throws Exception
	 */
	@Test
	public void test_executeTask_ValidHost_ValidBoardMapping() throws Exception
	{
		logger.info("TS: SelfTestTaskTest: test_executeTask_ValidHost_ValidBoardMapping");
		TaskResponse taskResponse = new TaskResponse(new ServerNode("N1", "10.28.197.202", "root", "root123"));
		
		SelfTestTask selfTestTask = new SelfTestTask(taskResponse);
		
		IBoardService boardService = new BoardService_S2600GZ();
		BoardServiceProvider.addBoardService(taskResponse.getNode().getServiceObject(), boardService, true);
		selfTestTask.executeTask();
				
		ServerNode node = (ServerNode) taskResponse.getNode();
		assertNotNull(node.getSelfTestResults());
		assertNotNull(node.getSelfTestResults().getSelfTestResultFailureCode());
		assertNotNull(node.getSelfTestResults().getSelfTestResultCode());
		
	}
	
	/**
	 * Test Case when Host is Valid, But there is Board Mapping for the node is Invalid
	 * @throws Exception
	 */
	@Test
	public void test_executeTask_ValidHost_InvalidBoardMapping() throws Exception
	{
		logger.info("TS: SelfTestTaskTest: test_executeTask_ValidHost_InvalidBoardMapping");
		TaskResponse taskResponse = new TaskResponse(new ServerNode("N1", "10.28.197.202", "root", "root123"));
		SelfTestTask selfTestTask = new SelfTestTask(taskResponse);
		
		IBoardService boardService = new BoardService_iLO();
		BoardServiceProvider.addBoardService(taskResponse.getNode().getServiceObject(), boardService, true);
		selfTestTask.executeTask();
				
		ServerNode node = (ServerNode) taskResponse.getNode();
		
		assertNull(node.getSytemBootOptions());
		
	}
	
	/**
	 * Test Case TaskResponse Object is Null
	 * @throws Exception
	 */
	@Test(expected = NullPointerException.class)
	public void test_executeTask_InvalidTaskResponse() throws Exception
	{
		logger.info("TS: SelfTestTaskTest: test_executeTask_InvalidTaskResponse");
		TaskResponse taskResponse = null;
		ServerNode sNode = new ServerNode("N1", "10.28.197.202", "root", "root123");
		SelfTestTask selfTestTask = new SelfTestTask(taskResponse);
		
		IBoardService boardService = new BoardService_S2600GZ();
		BoardServiceProvider.addBoardService(sNode.getServiceObject(), boardService, true);
		selfTestTask.executeTask();
	}
	
}
