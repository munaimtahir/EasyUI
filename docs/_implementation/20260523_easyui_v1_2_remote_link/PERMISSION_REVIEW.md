# EasyUI V1.2 Permission Review

## Existing Permissions
- `INTERNET`: Remained unchanged.
- `QUERY_ALL_PACKAGES`: Remained unchanged.

## Added Permissions
- None. The Remote Link mechanism uses Deep Links and Intent sharing, which do not require additional manifest permissions.

## Play Store Risk Notes
- Using Deep Links is a standard and safe practice for linking between app instances.
- The use of the System Share sheet ensures that the user is in control of data transmission, which is favorable for Play Store privacy reviews.
