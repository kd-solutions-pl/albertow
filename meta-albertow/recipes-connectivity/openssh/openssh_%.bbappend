FILESEXTRAPATHS:prepend := "${THISDIR}/openssh:"

SRC_URI:append = " \
    file://sshdgenkeys-data.conf \
"

OPENSSH_HOST_KEY_DIR_READONLY_CONFIG = "/data/ssh"

do_install:append() {
    install -d ${D}${systemd_system_unitdir}/sshdgenkeys.service.d
    install -m 0644 ${UNPACKDIR}/sshdgenkeys-data.conf \
        ${D}${systemd_system_unitdir}/sshdgenkeys.service.d/data.conf
}
