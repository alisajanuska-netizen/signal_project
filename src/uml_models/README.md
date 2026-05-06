## 1. Alert Generation System Explanation

The Alert Generation System checks patient data and creates an alert when something looks unsafe. The main class is AlertGenerator. It reads patient information, checks the patient’s data, and decides if an alert is needed. I used evaluateData(patient: Patient): void because the alert system should look at the patient’s records before making a decision.

ThresholdManager and AlertRule are separate classes so the alert limits are not placed directly inside AlertGenerator. This makes the system easier to change later. For example, different patients may need different heart rate or blood pressure limits. If a patient’s data breaks one of the rules, AlertGenerator creates an Alert.

The private method triggerAlert(alert: Alert): void is included because triggering an alert should be done inside AlertGenerator. Other classes should not call it directly. After an alert is created, it is sent to AlertManager. AlertManager keeps track of active alerts and dispatches them.

Patient, PatientData, and DataStorage show where the alert system gets patient readings from. Overall, this design keeps the parts simple: one class checks data, one class stores rules, one class represents alerts, and one class manages alert delivery.

## 2. Data Storage System Explanation

The Data Storage System stores patient readings and allows them to be found later. I made DataStorage an interface because the assignment describes it as the main storage interface. This means other parts of the system can use storage without needing to know exactly how the data is saved.

SecureDataStorage is the main storage class. It uses PatientDataRepository to store and search records. PatientDataRepository holds many PatientData records. Each PatientData object represents one patient reading. It has a patient ID, signal type, value, timestamp, and version. I added version: int because the assignment asks for data versioning.

DataRetriever is used to get stored data, such as patient history or the latest reading. This is useful for medical staff who need to review patient information. AccessController and User show a simple way to control who can access the data. A user has a role, and the access controller checks if that role is allowed.

I also added deleteOlderThan(days: int) to show the data deletion policy. This helps remove old records after a set amount of time. Overall, this diagram shows basic storage, retrieval, access control, versioning, and deletion without making the design too complicated.

## 3. Patient Identification System Explanation

The Patient Identification System connects incoming data to the correct hospital patient. The main class is PatientIdentifier. It receives a RawSignalRecord and tries to match it with a patient in PatientRegistry.

RawSignalRecord represents data from the simulator before it is fully checked. It includes the source patient ID, signal type, and raw value. PatientRegistry stores hospital patients and can find a patient by ID. HospitalPatient stores simple patient details, such as hospital patient ID, name, and medical history.

MatchResult shows whether the match worked or failed. I added patient: HospitalPatient so the system can store the matched patient when the match is successful. If the match fails, the reason field can explain the problem.

IdentityManager controls the matching process. It uses PatientIdentifier and can handle bad or missing matches. MismatchHandler is used when the system cannot match the data to a real patient. This keeps error handling separate from normal patient lookup.

Overall, the design is simple: raw data comes in, the system checks the patient ID, returns a match result, and handles problems if the patient cannot be found.

## 4. Data Access Layer Explanation

The Data Access Layer shows how patient data enters the system. The assignment says data can come from TCP, WebSocket, or file logs, so I used one DataListener interface and three listener classes: TCPDataListener, WebSocketDataListener, and FileDataListener.

Each listener has the same basic methods: start(), stop(), and readData(). This keeps the design simple. Each listener only stores what it needs. For example, TCPDataListener has a port, WebSocketDataListener has an endpoint, and FileDataListener has a file path.

DataSourceAdapter connects the listener to the rest of the system. It gets raw data from a listener, sends it to DataParser, and then forwards the parsed data to DataStorage.

DataParser is separate because reading data and parsing data are different jobs. The parser turns raw text into PatientData and checks if the format is valid.

This design keeps the input layer separate from the rest of the CHMS. It also makes the system easier to change later. If a new data source is needed, a new listener can be added without changing the whole system.