SUMMARY = "Password hash generator from Debian whois"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=570a9b3749dd0463a1778803b12a6dce"

SRC_URI = "https://deb.debian.org/debian/pool/main/w/whois/whois_${PV}.tar.xz"
SRC_URI[sha256sum] = "121a3b0467ad64a0e7034b44e71bd1cf28a2e4cca82886d40804ce1fac6494c0"

S = "${UNPACKDIR}/work"

inherit pkgconfig

DEPENDS += "virtual/crypt"

do_compile() {
    printf '#define VERSION "%s"\n' "${PV}" > ${S}/version.h
    oe_runmake -C ${S} mkpasswd
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/mkpasswd ${D}${bindir}/mkpasswd
}
