SUMMARY = "Administrative system user"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit allarch useradd

ADMIN_USER ??= "admin"
ADMIN_USER_PASSWORD ??= "\$6\$admin\$Hj3N8I1sKGMiE2GdZL/gozXq/BaoERQy.NXdYkeJJwDuqRD/bTT6tKolDMLVYoQQRVFjdvRo52L9mB/1QsYTk."

ALLOW_EMPTY:${PN} = "1"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system wheel"
USERADD_PARAM:${PN} = "--system --create-home --shell /bin/sh --groups wheel --password '${ADMIN_USER_PASSWORD}' ${ADMIN_USER}"
