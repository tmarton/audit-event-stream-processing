package net.atos.tmarton.eventprocessing;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;

/**
 * Created by tmarton on 3/13/16.
 */
public class TransformedAuditEvent {

    // information about machine that transform event
    private String version;
    private Date timestamp;
    private String host;
    private Integer port;

    // information about action
    private String operation;
    private String type;
    private String authnMethod;
    private Date identificationWhen;
    private String identificationUid;
    private int identificationOutcome;
    private String identificationSource;

    // information about machine that serves action
    private String whereFromApplication;
    private String whereFromAddress;

    // information about user that took action
    private String whoDn;
    private String whoFromAddress;
    private String whoName;
    private String whoUid;
    private String whoSn;
    private String whoEmployeeNumber;


    @JsonProperty("@version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("@timestamp")
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthnMethod() {
        return authnMethod;
    }

    public void setAuthnMethod(String authnMethod) {
        this.authnMethod = authnMethod;
    }

    public Date getIdentificationWhen() {
        return identificationWhen;
    }

    public void setIdentificationWhen(Date identificationWhen) {
        this.identificationWhen = identificationWhen;
    }

    public String getIdentificationUid() {
        return identificationUid;
    }

    public void setIdentificationUid(String identificationUid) {
        this.identificationUid = identificationUid;
    }

    public int getIdentificationOutcome() {
        return identificationOutcome;
    }

    public void setIdentificationOutcome(int identificationOutcome) {
        this.identificationOutcome = identificationOutcome;
    }

    public String getIdentificationSource() {
        return identificationSource;
    }

    public void setIdentificationSource(String identificationSource) {
        this.identificationSource = identificationSource;
    }

    public String getWhereFromApplication() {
        return whereFromApplication;
    }

    public void setWhereFromApplication(String whereFromApplication) {
        this.whereFromApplication = whereFromApplication;
    }

    public String getWhereFromAddress() {
        return whereFromAddress;
    }

    public void setWhereFromAddress(String whereFromAddress) {
        this.whereFromAddress = whereFromAddress;
    }

    public String getWhoDn() {
        return whoDn;
    }

    public void setWhoDn(String whoDn) {
        this.whoDn = whoDn;
    }

    public String getWhoFromAddress() {
        return whoFromAddress;
    }

    public void setWhoFromAddress(String whoFromAddress) {
        this.whoFromAddress = whoFromAddress;
    }

    public String getWhoName() {
        return whoName;
    }

    public void setWhoName(String whoName) {
        this.whoName = whoName;
    }

    public String getWhoUid() {
        return whoUid;
    }

    public void setWhoUid(String whoUid) {
        this.whoUid = whoUid;
    }

    public String getWhoSn() {
        return whoSn;
    }

    public void setWhoSn(String whoSn) {
        this.whoSn = whoSn;
    }

    public String getWhoEmployeeNumber() {
        return whoEmployeeNumber;
    }

    public void setWhoEmployeeNumber(String whoEmployeeNumber) {
        this.whoEmployeeNumber = whoEmployeeNumber;
    }
}
