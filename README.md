# SMPP_BRIDGE_USSD



SMPP :
- SMPP or **Short Message Peer to Peer protocol** is an **open standard** protocol that uses **TCP/IP protocol** for the transport of protocol messages. Earlier to SMPP, ss7 was the standard protocol for messaging over the GSM network.
- The SMPP (Short Message Peer-to-Peer Protocol) provides you with an environment to send and receive short messages.
- The SMPP (Short Message Peer-to-Peer) protocol  designed to provide a flexible data communications interface for the transfer of short message data between External Short Message Entities (ESME), Routing Entities (RE), and Message Centres (MC). It is a means by which applications can send and receive SMS messages to and from mobile devices.
- Applications do this using an SMPP connection to a Short Message Service Center (SMSC), [SMS gateway](https://smpp.org/sms-gateway.html), SMPP gateway or hub
- USSD and SMS both can use SMPP. A messaging application can be developed using SMPP. Two peers can develop applications independently.
- SMPP is designed to support short messaging functionality for any cellular technology and has specific applications and features for technologies such as: GSM, UMTS, LTE, IS-95 (CDMA)
- In order to make use of the SMPP Protocol, an SMPP session must be established between the ESME and Message Centre or SMPP Routing Entity where appropriate.
- The established session is based on an application layer TCP/IP connection between the ESME and MC/RE and is usually initiated by the ESME. The connection is often over the Internet and can use SMPP over TLS or a VPN to secure the connection.
- There are three forms of ESME-initiated sessions:
- **Transmitter** (**TX**) - when authenticated as a transmitter, an ESME may submit short messages to the MC for onward delivery to Mobile Stations (MS). A transmitter session will also allow an ESME cancel, query or replace previously submitted messages. Messages sent in this manner are often called mobile terminated messages.
- **Receiver** (**RX**) - a receiver session enables an ESME to receive messages from an MC. These messages typically originate from mobile stations and are referred to as mobile originated messages.
- **Transceiver** (**TRX**) - a TRX session is a combination of TX and RX, such that a single SMPP session can be used to submit mobile terminated messages and receive mobile originated messages.
- The SMPP protocol is a set of operations, each one taking the form of a request and response Protocol Data Unit (PDU) containing an SMPP command. For example, if an ESME wishes to submit a short message, it may send a `submit_sm` PDU to the MC. The MC responds with a `submit_sm_resp` PDU, indicating the success or failure of the request. Likewise, if an MC wishes to deliver a message to an ESME, it may send a `deliver_sm` PDU to an ESME, which in turn responds with a `deliver_sm_resp` PDU as a means of acknowledging the delivery.


USSD :

![image](https://user-images.githubusercontent.com/100505947/231861785-a4ed4451-bdc9-4ea0-92ac-4bc9afdc8fbf.png)







References :

- [GitHub] https://github.com/OpenSmpp/opensmpp
- [Manual] http://opensmpp.org/specifications.html
