# EasyUI V1.2 Remote Link Discovery Report

## Overview
V1.2 aims to create a "Remote Link" between a Senior's phone and a Caregiver's phone. Since we are avoiding a cloud backend, we will use a **Deep Link + Share Sheet** mechanism.

## Mechanism: "Local-First Remote Link"
1. **Generation (Senior Side)**: The app packages current `PhoneHealthState` and `SetupCompleteness` into a JSON packet.
2. **Encoding**: The JSON is Base64 encoded and attached to a deep link URI: `easyui://status?v=1&d=<BASE64>`.
3. **Sharing**: The Senior (or Caregiver during setup) shares this link via SMS, WhatsApp, etc.
4. **Reception (Caregiver Side)**: When the Caregiver taps the link, EasyUI opens, decodes the data, and saves it as a "Linked Device" entry.
5. **Viewing**: The Caregiver can see a list of "Linked Devices" and their last reported status.

## Architecture Changes
- **core:domain**:
  - `RemoteStatusPacket`: Data model for the shared status.
  - `LinkedDevice`: Model for a registered remote phone.
  - `RemoteLinkRepository`: Interface for managing linked devices.
- **core:data**:
  - `DataStoreRemoteLinkRepository`: Persistent storage for linked devices.
- **app**:
  - Deep Link handling in `MainActivity` and `EasyUiNavGraph`.
  - `RemoteLinkViewModel` to manage the logic.

## UI Enhancements
- **Senior Home**: Optional "Update Caregiver" button in Phone Health card.
- **Caregiver Tools**: 
  - "My Linked Devices" section.
  - "Share My Status" section.

## Limitations
- **Manual Sync**: Without a cloud, updates are not automatic. The senior must "Share" or the caregiver must "Request" (which sends an SMS/message that the senior then responds to).
- **Latency**: Status is only as fresh as the last share.

## Implementation Plan
1. **Foundation**: Models and Repository.
2. **Senior Side**: Export logic and "Share" UI.
3. **Caregiver Side**: Deep link handling and "Linked Devices" UI.
4. **Verification**: Unit tests for encoding/decoding and repository persistence.
