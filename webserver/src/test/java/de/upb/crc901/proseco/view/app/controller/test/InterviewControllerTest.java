package de.upb.crc901.proseco.view.app.controller.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import de.upb.crc901.proseco.commons.interview.InterviewFillout;
import de.upb.crc901.proseco.commons.processstatus.InvalidStateTransitionException;
import de.upb.crc901.proseco.commons.util.SerializationUtil;
import de.upb.crc901.proseco.view.app.controller.InterviewController;
import de.upb.crc901.proseco.view.app.model.InterviewDTO;
import de.upb.crc901.proseco.view.app.model.StrategyCandidateFoundEvent;

public class InterviewControllerTest {

	private static final String INTERVIEW_STATE_PATH = "processes/test-default/interview/interview_state.json";

	@Test
	public void initTest() {
		final InterviewController controller = new InterviewController();
		final Model model = new ExtendedModelMap();
		final String response = controller.init(model);
		assertEquals("initiator", response);
	}

	@Test
	public void indexTest() {
		final InterviewController controller = new InterviewController();
		final String response = controller.index();
		assertEquals("index", response);
	}

	@Test
	public void strategyChartDirectiveTest() {
		final InterviewController controller = new InterviewController();
		final String response = controller.strategyChartDirective();
		assertEquals("strategy/strategyChart", response);
	}

	@Test
	public void initSubmitTest() throws InvalidStateTransitionException {
		final InterviewController controller = new InterviewController();
		final InterviewDTO interviewDTO = new InterviewDTO();
		interviewDTO.setContent("test");
		final String response = controller.initSubmit(interviewDTO);
		assertEquals("result", response);
	}

	@Ignore
	@Test
	public void nextTest() throws InvalidStateTransitionException {
		final InterviewController controller = new InterviewController();
		final InterviewDTO interviewDTO = new InterviewDTO();
		interviewDTO.setContent("test");
		final String response = controller.next("test-default", interviewDTO);
		assertEquals("result", response);
	}

	@Test
	public void prevTest() {
		final InterviewController controller = new InterviewController();
		final String response = controller.prev(new InterviewDTO());
		assertEquals("result", response);
	}

	@Test
	public void nextPostTest() throws InvalidStateTransitionException {
		final InterviewController controller = new InterviewController();
		final InterviewDTO interviewDTO = new InterviewDTO();
		interviewDTO.setContent("test");
		controller.initSubmit(interviewDTO);
		controller.next("test-default", interviewDTO);
		final String response = controller.nextPost("test-default", interviewDTO, "response", null);
		assertEquals("result", response);
	}

	@Test
	public void nextPostFileTest() throws InvalidStateTransitionException, FileNotFoundException, IOException {
		final InterviewController controller = new InterviewController();
		final InterviewDTO interviewDTO = new InterviewDTO();
		interviewDTO.setContent("test");
		controller.initSubmit(interviewDTO);
		final File testFile = new File("testdata/test.txt");
		final DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("fileData", "text/plain", true, testFile.getName());
		try (InputStream input = new FileInputStream(testFile)) {
			final OutputStream os = fileItem.getOutputStream();
			int ret = input.read();
			while (ret != -1) {
				os.write(ret);
				ret = input.read();
			}
			os.flush();
		}

		final MultipartFile file = new CommonsMultipartFile(fileItem);
		controller.nextPost("test-default", interviewDTO, null, file);
		controller.nextPost("test-default", interviewDTO, "test", null);
		controller.nextPost("test-default", interviewDTO, "test", null);
		controller.nextPost("test-default", interviewDTO, "test", null);
		final String response = controller.nextPost("test-default", interviewDTO, "test", null);
		assertEquals("result", response);
	}

	@Test
	public void postCandidateFoundEventTest() throws InvalidStateTransitionException {
		final InterviewController controller = new InterviewController();
		final StrategyCandidateFoundEvent e = new StrategyCandidateFoundEvent();
		final ResponseEntity<Object> response = controller.postCandidateFoundEvent("test-default", e);
		assertEquals(HttpStatus.OK, response.getStatusCode());

	}

	@Test
	public void processIDTest() {
		final InterviewController controller = new InterviewController();
		final InterviewDTO interviewDTO = new InterviewDTO();
		interviewDTO.setContent("test");
		interviewDTO.setProcessId("test-default");
		final Map<String, String> response = controller.processID(interviewDTO);
		assertEquals("test-default", response.get("processID"));

	}

	@Test
	public void getEvaluationsSortedByTimestampTest() {
		final InterviewController controller = new InterviewController();
		final ResponseEntity<Object> response = controller.getEvaluationsSortedByTimestamp("test-default");
		assertEquals(HttpStatus.OK, response.getStatusCode());

	}

	@Test
	public void processStatusTest() {
		final InterviewController controller = new InterviewController();
		final Map<String, String> response = controller.processStatus("test-default");
		assertEquals("domain", response.get("status"));

	}

	@Test
	public void setProcessStatusTest() {
		final InterviewController controller = new InterviewController();
		final Map<String, String> statusMap = new HashMap<>();
		statusMap.put("status", "domain");
		final ResponseEntity<Object> response = controller.setProcessStatus("test-default", statusMap);
		assertEquals(HttpStatus.OK, response.getStatusCode());

	}

	@Test
	public void pushResultTest() {
		final InterviewController controller = new InterviewController();
		final ResponseEntity<Object> response = controller.pushResult("test-default");
		assertEquals(HttpStatus.OK, response.getStatusCode());

	}

	@Ignore
	@Test
	public void testFileUpload() throws IOException, InvalidStateTransitionException {

		// delete file if exists
		File uploadedFile = new File("processes/test-default/interview/res/file");
		if (uploadedFile.exists()) {
			Files.delete(uploadedFile.toPath());
		}

		// update interview state to file question
		final File src = new File("testdata/interview_state_file.json");
		final File dst = new File(INTERVIEW_STATE_PATH);
		Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);

		final InterviewController controller = new InterviewController();
		final InterviewDTO interviewDTO = new InterviewDTO();
		interviewDTO.setContent("test");
		controller.initSubmit(interviewDTO);
		final File testFile = new File("testdata/test.txt");
		final DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("fileData", "text/plain", true, testFile.getName());
		try (InputStream input = new FileInputStream(testFile)) {
			final OutputStream os = fileItem.getOutputStream();
			int ret = input.read();
			while (ret != -1) {
				os.write(ret);
				ret = input.read();
			}
			os.flush();
		}

		final MultipartFile file = new CommonsMultipartFile(fileItem);
		controller.nextPost("test-default", interviewDTO, null, file);

		uploadedFile = new File("processes/test-default/interview/res/file");
		assertTrue(uploadedFile.exists());

	}

	@Ignore
	@Test
	public void testSimpleInput() throws IOException, InvalidStateTransitionException {
		// update interview state to simple question
		final File src = new File("testdata/interview_state_simple.json");
		final File dst = new File(INTERVIEW_STATE_PATH);
		Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);

		final InterviewController controller = new InterviewController();
		final InterviewDTO interviewDTO = new InterviewDTO();
		interviewDTO.setContent("test");
		controller.initSubmit(interviewDTO);
		controller.nextPost("test-default", interviewDTO, "test", null);
		final File interviewState = new File(INTERVIEW_STATE_PATH);
		final InterviewFillout interview = SerializationUtil.readAsJSON(interviewState);
		interview.getAnswers();
		assertEquals("step1", interview.getCurrentState().getName());

	}

}