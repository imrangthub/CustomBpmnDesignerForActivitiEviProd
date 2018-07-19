package com.example;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.activiti.bpmn.constants.BpmnXMLConstants;
import org.activiti.engine.RepositoryService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Component
public class XMLWriterDom implements BpmnXMLConstants {

	private DocumentBuilderFactory docFactory;
	private DocumentBuilder docBuilder;
	private Document doc;
	private Resource resource;

	private Element edgeElement;
	private TransformerFactory transformerFactory;
	private Transformer transformer;
	private DOMSource domSource;
	private StreamResult streamResult;

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public String getBpmnFile() throws SAXException, IOException {
		String s = null;
		try {
			resource = new ClassPathResource("/processes/tasks.bpmn20.xml");
			LOG.debug(resource.getFilename());
			this.getXmlDocument();

			NodeList userTaskNodeList = this.getNodeList(ELEMENT_TASK_USER);
			NodeList flowNodeList = this.getNodeList(ELEMENT_SEQUENCE_FLOW);

			this.removeFlowElement(flowNodeList);
			this.addUserTask(userTaskNodeList);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.writeXmlDocument();
		return s;
	}

	private void addUserTask(NodeList userTaskNodeList) {
		String source = "usertask1";
		String target = "usertask2";
		Element newUserTask = doc.createElement(ELEMENT_TASK_USER);
		newUserTask.setAttribute(ATTRIBUTE_ID, "usertask2");
		newUserTask.setAttribute(ATTRIBUTE_NAME, "Task-02");
		Element userTaskParentElement = (Element) userTaskNodeList.item(0).getParentNode();
		userTaskParentElement.appendChild(newUserTask);
		createUserTaskShape(target);
		addSequenceFlow(userTaskParentElement, source, target);
	}

	private void createUserTaskShape(String target) {

		NodeList n = doc.getElementsByTagName(BPMNDI_PREFIX + ":" + ELEMENT_DI_PLANE);
		Element e = (Element) n.item(0);

		NodeList userShapeList = getNodeList(BPMNDI_PREFIX + ":" + ELEMENT_DI_SHAPE);
		Node userShapeNode = userShapeList.item(0);

		Element userShape = (Element) userShapeNode.cloneNode(true);
		userShape.setAttribute(ATTRIBUTE_DI_BPMNELEMENT, target);
		userShape.setAttribute(ATTRIBUTE_ID, ELEMENT_DI_SHAPE + "_" + target);
		e.appendChild(userShape);
	}

	private void addSequenceFlow(Element userTaskParentElement, String source, String target) {
		Element newFlowElement = doc.createElement(ELEMENT_SEQUENCE_FLOW);
		newFlowElement.setAttribute(ATTRIBUTE_ID, "flow5");
		newFlowElement.setAttribute(ATTRIBUTE_FLOW_SOURCE_REF, source);
		newFlowElement.setAttribute(ATTRIBUTE_FLOW_TARGET_REF, target);
		userTaskParentElement.appendChild(newFlowElement);
		newFlowElement = doc.createElement(ELEMENT_SEQUENCE_FLOW);
		newFlowElement.setAttribute(ATTRIBUTE_ID, "flow6");
		newFlowElement.setAttribute(ATTRIBUTE_FLOW_SOURCE_REF, target);
		newFlowElement.setAttribute(ATTRIBUTE_FLOW_TARGET_REF, "usertask3");
		userTaskParentElement.appendChild(newFlowElement);

		NodeList n = doc.getElementsByTagName(BPMNDI_PREFIX + ":" + ELEMENT_DI_PLANE);
		Element e = (Element) n.item(0);

		edgeElement.setAttribute(ATTRIBUTE_DI_BPMNELEMENT, "flow5");
		edgeElement.setAttribute(ATTRIBUTE_ID, ELEMENT_DI_EDGE + "_flow5");
		e.appendChild(edgeElement);

		Element el1 = (Element) edgeElement.cloneNode(true);
		el1.setAttribute(ATTRIBUTE_DI_BPMNELEMENT, "flow6");
		el1.setAttribute(ATTRIBUTE_ID, ELEMENT_DI_EDGE + "_flow6");
		e.appendChild(el1);

	}

	private void getXmlDocument() {

		try {
			this.docFactory = DocumentBuilderFactory.newInstance();
			this.docBuilder = docFactory.newDocumentBuilder();
			this.doc = docBuilder.parse(this.resource.getInputStream());
			this.doc.getDocumentElement().normalize();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private NodeList getNodeList(String parentNodeName) {
		return this.doc.getElementsByTagName(parentNodeName);
	}

	private void removeFlowElement(NodeList flowNodeList) {
		String flowSequenceId = null;
		for (int i = 0; i < flowNodeList.getLength(); i++) {
			Node flowNode = flowNodeList.item(i);
			Element flowElement = (Element) flowNode;
			if (flowElement.getAttribute(ATTRIBUTE_FLOW_SOURCE_REF).equals("usertask1")) {
				flowSequenceId = flowElement.getAttribute(ATTRIBUTE_ID);
				flowElement.getParentNode().removeChild(flowElement);
			}
		}
		NodeList edgeList = this.getNodeList(BPMNDI_PREFIX + ":" + ELEMENT_DI_EDGE);
		for (int i = 0; i < edgeList.getLength(); i++) {
			Node edgeNode = edgeList.item(i);
			edgeElement = (Element) edgeNode;
			LOG.debug(edgeElement.getTagName());
			if (edgeElement.getAttribute(ATTRIBUTE_DI_BPMNELEMENT).equals(flowSequenceId)) {
				edgeElement.getParentNode().removeChild(edgeElement);
			}
		}

	}

	private void writeXmlDocument() throws SAXException, IOException {
		try {
			this.transformerFactory = TransformerFactory.newInstance();
			this.transformer = this.transformerFactory.newTransformer();
			this.domSource = new DOMSource(doc);
			// resource = new
			// ClassPathResource("/tempProcess/Temp_loanProcess.xml");
			LOG.debug("URI " + resource.getURI());
			LOG.debug("URL " + resource.getURL());
			this.streamResult = new StreamResult(new File(resource.getURI()));
			StreamResult result = new StreamResult(System.out);
			transformer.transform(domSource, streamResult);
			transformer.transform(domSource, result);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
