package org.myrobotlab.framework;


import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import org.junit.BeforeClass;
import org.junit.Test;
import org.myrobotlab.logging.Appender;
import org.myrobotlab.logging.Level;
import org.myrobotlab.logging.LoggerFactory;
import org.myrobotlab.logging.Logging;
import org.myrobotlab.logging.LoggingFactory;
import org.myrobotlab.service.Runtime;
import org.myrobotlab.service.TestCatcher;
import org.myrobotlab.service.TestThrower;
import org.myrobotlab.service.interfaces.CommunicationInterface;
import org.slf4j.Logger;

public class MessageTest {
	public final static Logger log = LoggerFactory.getLogger(MessageTest.class);

	static TestCatcher catcher;
	static TestThrower thrower;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		LoggingFactory.getInstance().configure();
		LoggingFactory.getInstance().setLevel(Level.INFO);
		catcher = (TestCatcher)Runtime.start("catcher", "TestCatcher");
		thrower = (TestThrower)Runtime.start("thrower", "TestThrower");
	}


	@Test
	public void simpleSubscribeAndThrow() throws Exception {

		catcher.clear();
		catcher.subscribe("thrower", "pitch");
		// ROUTE MUST STABALIZE - BEFORE MSGS - otherwise they will be missed
		Service.sleep(100);
		
		thrower.pitchInt(1000);
		BlockingQueue<Message> balls = catcher.waitForMsgs(1000);

		log.warn(String.format("caught %d balls", balls.size()));
		log.warn(String.format("left balls %d ", catcher.msgs.size()));
	}

	@Test
	public void broadcastMessage() throws Exception {
		catcher.clear();
		catcher.subscribe("thrower", "pitch");
		
		Message msg = thrower.createMessage(null, "getServiceNames", null);
		CommunicationInterface comm = thrower.getComm();
		comm.send(msg);
		
		String[] ret = (String[])thrower.invoke(msg);
		log.info(String.format("got %s", Arrays.toString(ret)));
		assertNotNull(ret);
	}
	
	/**
	 * test to verify we can remove all message routes
	 * @throws Exception
	 */
	@Test
	static final public void clearRoutes() throws Exception {
		catcher.clear();
		catcher.subscribe("thrower", "pitch");
		
		// "long" pause to make sure our message route is in
		Service.sleep(100);
		
		thrower.pitchInt(1000);
		BlockingQueue<Message> balls = catcher.waitForMsgs(1000);

		log.warn(String.format("caught %d balls", balls.size()));
		log.warn(String.format("left balls %d ", catcher.msgs.size()));
		
		Runtime.removeAllSubscriptions();
		
		Message msg = thrower.createMessage(null, "getServiceNames", null);
		CommunicationInterface comm = thrower.getComm();
		comm.send(msg);
		
		String[] ret = (String[])thrower.invoke(msg);
		log.info(String.format("got %s", Arrays.toString(ret)));
		assertNotNull(ret);
		
		catcher.clear();
		
		// "long" pause to make sure our message route is in
		Service.sleep(100);
		
		thrower.pitchInt(1000);
		
		Service.sleep(100);
		assertEquals(0, catcher.msgs.size());

	}
	
	@Test
	static public void badNameTest() throws Exception {
		catcher.clear();
		TestCatcher catcher2 = null;
		try {
			Runtime.start("myName/isGeorge", "TestCatcher");
		} catch (Exception e){
			// Logging.logError(e);
			log.info("good bad name threw");
		}
		assertNull(catcher2);
	}
	
	@Test
	static public void invokeStringNotation() throws Exception {
		catcher.clear();
		// FIXME - implement
		// catcher.subscribe("thrower/pitch");
		
		catcher.clear();
		catcher.subscribe("thrower", "pitch");
		Service.sleep(100);
		
		thrower.pitchInt(1000);
		BlockingQueue<Message> balls = catcher.waitForMsgs(1000);
		
		assertEquals(1000, balls.size());
		
		/*
		
		Runtime runtime = Runtime.getInstance();
		
		Message msg = thrower.createMessage(null, "getServiceNames", null);
		CommunicationInterface comm = thrower.getComm();
		comm.send(msg);
		
		String[] ret = (String[])thrower.invoke(msg);
		log.info(String.format("got %s", Arrays.toString(ret)));
		assertNotNull(ret);
		*/
	}
	
	/**
	 * test to excercise
	 * @throws Exception
	 */
	@Test
	static public void RuntimeTests() throws Exception {
		catcher.clear();
		// FIXME - implement
		// catcher.subscribe("thrower/pitch");
		
		catcher.clear();
		catcher.subscribe("thrower", "pitch");
		Service.sleep(100);
		
		thrower.pitchInt(1000);
		BlockingQueue<Message> balls = catcher.waitForMsgs(1000);
		
		Runtime runtime = Runtime.getInstance();
		
		Message msg = thrower.createMessage(null, "getServiceNames", null);
		CommunicationInterface comm = thrower.getComm();
		comm.send(msg);
		
		String[] ret = (String[])thrower.invoke(msg);
		log.info(String.format("got %s", Arrays.toString(ret)));
		assertNotNull(ret);
	}
	
	
	public static void main(String[] args) {
		try {
			
			LoggingFactory.getInstance().configure();
			LoggingFactory.getInstance().setLevel(Level.DEBUG);
			Logging logging = LoggingFactory.getInstance();
			logging.addAppender(Appender.FILE);
			
			setUpBeforeClass();
			//clearRoutes();
			// badNameTest();
			invokeStringNotation();
		

		} catch(Exception e){
			Logging.logError(e);
		}
		
		System.exit(0);
	}
}