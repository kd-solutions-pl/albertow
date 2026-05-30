FILESEXTRAPATHS:prepend := "${THISDIR}/systemd:"
SRC_URI:append = " \
    file://0001-Fix-tmpfiles-related-warnings.patch \
"

PACKAGECONFIG:remove = "networkd timesyncd"

EXTRA_OEMESON:append = " -Dhwdb=false"
RRECOMMENDS:${PN}:remove = "udev-hwdb ${PN}-mime"

do_install:append() {
    rm -f ${D}${nonarch_libdir}/tmpfiles.d/legacy.conf
}
