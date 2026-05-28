SUMMARY = "PAM module for authentication with an /etc/passwd-like file"
HOMEPAGE = "https://git.tiwe.de/libpam-pwdfile.git"
LICENSE = "BSD-3-Clause | GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://pam_pwdfile.c;beginline=1;endline=39;md5=02765d16df913d327ffd4a2ef499baf4"

DEPENDS = "libpam virtual/crypt"

SRC_URI = "git://git.tiwe.de/libpam-pwdfile.git;protocol=https;branch=master;tag=v${PV}"
SRCREV = "2347ef0edd7054d3df9838612aa78ac6bd077dfd"

do_compile() {
    oe_runmake \
        CC="${CC}" \
        CFLAGS="${CFLAGS} -fPIC -fvisibility=hidden" \
        CPPFLAGS="${CPPFLAGS} -DUSE_CRYPT_R -D_FILE_OFFSET_BITS=64" \
        LDFLAGS="${LDFLAGS} -Wl,-x -shared"
}

do_install() {
    oe_runmake install \
        DESTDIR="${D}" \
        PAM_LIB_DIR="${base_libdir}/security"
}

FILES:${PN} += "${base_libdir}/security/pam_pwdfile.so"
RPROVIDES:${PN} += "pam-plugin-pwdfile"
