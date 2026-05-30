FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://sudoers.in"

do_install:append () {
    install -m 0440 ${UNPACKDIR}/sudoers.in ${D}/etc/sudoers
}
