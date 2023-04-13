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

**Components of OpenSMPP Used :**

- Gateway End:
  - TCPIPConnection
  - BindRequest
  - BindResponse
  - DeliverSM
  - TLVOctets
  - DeliverSMResp
  - BindTransciever
  - PDU
  - Session

- Mobile station End:
  - TCPIPConnection
  - BindRequest
  - BindResponse
  - DeliverSM
  - TLVOctets
  - DeliverSMResp
  - BindTransciever
  - PDU
  - Session

- SMSC End :
  - SMSCSession
  - SMSCListener
  - SimulatorPDUProcessorFactory
  - PDUProcessorGroup
  - PDU
  - DeliveryInfoSender
  - BufferedReader
  - Connection
  - ReceiverBase
  - Receiver
 
  




**Flow of Execution :**
![image](https://user-images.githubusercontent.com/100505947/231870385-f31a3970-4efe-4ee8-8dc4-1724cf6f3760.png)

**Execution Steps:**
1. Clone the smppsender ,smpp reciever and smpp simulator(smsc) project
2. Run the smsc, smsc sender ,and smpp reciever projects separately in that order.
3. Enter *123# in the sender 

SENDER(MobileStation) :
![image](https://user-images.githubusercontent.com/100505947/231871864-746a44e6-06df-49e3-b5b2-ab12e1443e05.png)

RECIEVER(Gateway) :
![image](https://user-images.githubusercontent.com/100505947/231872096-c4649d3c-4565-436b-9b04-321153c2d53b.png)


SIMULATOR :
![image](https://user-images.githubusercontent.com/100505947/231871755-58c730ac-0d8d-4eec-883b-3b044f79379d.png)


4. choose appropriate option in the list of Options
![image](https://user-images.githubusercontent.com/100505947/231872307-d9ae32b8-5490-49c4-aed9-c951fdeea4a7.png)
![image](https://user-images.githubusercontent.com/100505947/231872371-56de456d-6dc2-4eff-b88f-ade9948827c4.png)
![image](https://user-images.githubusercontent.com/100505947/231872448-d88ed57e-3d04-47d2-8555-f4cfc4cdf2e0.png)


5. End the session of both sender and reciever.
![image](https://user-images.githubusercontent.com/100505947/231872534-9edc64e1-59fd-49db-bc72-ba345b5069a6.png)



**Problems faced :**
- Asynchronous flow of execution in Simulator blocking on I/O 
- Extraction of PDU from Abstract library.
- Session methods overriding.
- SMSC connection and listener management
- Setting and TLVs values

**Future Enhancement :**
- Integrate developed USSD with backend of Application in any domain  as per requirement.
- Now only 2 processes are connected Gateway and MobileStation. Can be extended to many mobile stations  

**References :**

- [GitHub] https://github.com/OpenSmpp/opensmpp
- [Manual] http://opensmpp.org/specifications.html
- [Youtube] https://www.youtube.com/@albprogramming8028
