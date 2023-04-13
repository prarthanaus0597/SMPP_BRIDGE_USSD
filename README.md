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
![image](https://user-images.githubusercontent.com/100505947/231862873-799ffd77-38c1-4349-b2e2-e5377bdb8de0.png)



USSD: ****(Unstructured Supplementary Service Data)****

- **USSD is a text menu driven technology allowing users to interact from their handset by making selections from a menu.**
- Unstructured Supplementary Service Data (USSD) A GSM communication technology used to send messages between a mobile phone and an application server in the network. It is very much similar to SMS, but USSD is session oriented as well as interactive.
- USSD is just like connection-oriented SMS communication i.e, USSD is to SMS what IM is to email. The initiation of the communication can either be USSD-PUSH ( Mobile-terminated & provider originated) or a USSD-PULL (Mobile originated & provider terminated)
- Every app requires a separate short code. USSD shortcodes are site addresses scheme similar to the website addresses in the internet world
- in India, USSD shortcodes are owned by the mobile service providers and one needs to get the service provider to configure the right short codes for you.
- USSD works using a connection oriented SMPP. However, USSD gateways ( service-provider owned middle-ware that relays USSD messages to and from the subscribers' mobiles, these days are capable of acting as bridges where their app interface can be over HTTP or HTTPS. In this case, I'd think you'd need HTTP or HTTPS connectivity opened between the mobile operator's gateway and your app. From then on, it is just matter of building a web-app with a text response!


![image](https://user-images.githubusercontent.com/100505947/231861785-a4ed4451-bdc9-4ea0-92ac-4bc9afdc8fbf.png)

Components of OpenSMPP Used :
- 
-
-



Flow of Execution :

Client(Mobile) ----------PSSR_IND(*123#)-------> SMSC-----PSSR_IND(*123#)-----> Application(Gateway)
                                                                                       **|**
Client(Mobile)<-----USSR_REQ(Menu)------SMSC <------------(Menu)---------------- Application(Gateway)
      **|**
Client(Mobile)----->USSR_ACK , Select value from menu ----------> SMSC------->Application(Gateway)
                                                                                       **|**
Client(Mobile) <---------- Info (USSR RES)-----------SMSC------(USSR RES)----------Application(Gateway)
      **|**
Client(Mobile) --------(PSSR_RESP)----------->SMSC----(PSSR_RESP)--------->Application(End session)



Problems faced :
- Asynchronous flow of execution in Simulator blocking on I/O 
- Extraction of PDU from Abstract library.
- Session methods overriding.
- SMSC connection and listener management
- Setting and TLVs values

Future Enhancement :
- Integrate developed USSD with backend of any domain  as per requirement.
- Now only 2 processes are connected Gateway and MobileStation. Can be extended to many mobile station  

References :

- [GitHub] https://github.com/OpenSmpp/opensmpp
- [Manual] http://opensmpp.org/specifications.html
