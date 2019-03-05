package gov.va.ocp.framework.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import gov.va.ocp.framework.transfer.AbstractTransferObject;
import gov.va.ocp.framework.transfer.DomainTransferObjectMarker;

/**
 * A base Request object capable of representing the payload of a service request.
 *
 * @see gov.va.ocp.framework.transfer.AbstractTransferObject
 * @author jshrader
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "serviceRequest")
public class DomainRequest extends AbstractTransferObject implements DomainTransferObjectMarker {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8521125059263688741L;

	/**
	 * Instantiates a new rest request.
	 */
	public DomainRequest() {
		super();
	}

}
