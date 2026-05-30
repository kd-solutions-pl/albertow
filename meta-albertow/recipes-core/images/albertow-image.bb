SUMMARY = "QEMU Image running systemd on read-only rootfs"
LICENSE = "MIT"

inherit core-image

IMAGE_FEATURES += "read-only-rootfs"

IMAGE_INSTALL = " \
    admin-user \
    sudo \
    ncurses-terminfo-base \
    packagegroup-core-boot \
    ${CORE_IMAGE_EXTRA_INSTALL} \
"

PACKAGE_EXCLUDE = "busybox-syslog busybox-klogd"
