SUMMARY = "Administrative system user"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit allarch systemd useradd

SRC_URI = " \
    file://admin-user-init-shadow \
    file://admin-user-init-shadow.service \
    file://admin-user-set-password.in \
"
S = "${UNPACKDIR}"
ADMIN_USER ??= "admin"
ADMIN_USER_PASSWORD ??= "\$y\$j9T\$6EtCxe7ejd3McRo.7eNUs0\$l93siWsz7QYHwRZsThzTqPtlePtn0N3OkTy90HiP.s."

RDEPENDS:${PN} += "libpam-runtime pam-plugin-pwdfile mkpasswd"
SYSTEMD_SERVICE:${PN} = "admin-user-init-shadow.service"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system wheel"
USERADD_PARAM:${PN} = "--system --create-home --shell /bin/sh --groups wheel --password '!' ${ADMIN_USER}"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/admin-user-init-shadow ${D}${sbindir}/admin-user-init-shadow
    sed -e 's|@ADMIN_USER@|${ADMIN_USER}|g' \
        -e 's|@ADMIN_USER_PWDFILE@|/data/security/shadow|g' \
        ${UNPACKDIR}/admin-user-set-password.in > ${D}${sbindir}/admin-user-set-password
    chmod 0755 ${D}${sbindir}/admin-user-set-password

    install -d ${D}${sysconfdir}/default
    printf '%s\n' \
        "ADMIN_USER='${ADMIN_USER}'" \
        "ADMIN_USER_PASSWORD='${ADMIN_USER_PASSWORD}'" \
        "ADMIN_USER_PWDFILE='/data/security/shadow'" \
        > ${D}${sysconfdir}/default/admin-user
    chmod 0600 ${D}${sysconfdir}/default/admin-user

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/admin-user-init-shadow.service \
        ${D}${systemd_system_unitdir}/admin-user-init-shadow.service
}

FILES:${PN} += " \
    ${sbindir}/admin-user-init-shadow \
    ${sbindir}/admin-user-set-password \
    ${sysconfdir}/default/admin-user \
    ${systemd_system_unitdir}/admin-user-init-shadow.service \
"
