FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append = " file://common-auth.in"

ADMIN_USER ??= "admin"

RDEPENDS:${PN}-runtime += "pam-plugin-pwdfile"

do_install:append() {
    sed -e 's|@ADMIN_USER_PWDFILE@|/data/security/shadow|g' \
        ${UNPACKDIR}/common-auth.in > ${D}${sysconfdir}/pam.d/common-auth
}
