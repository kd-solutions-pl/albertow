FILESEXTRAPATHS:prepend := "${THISDIR}/linux-yocto:"

SRC_URI:append = " file://squashfs-rootfs.cfg"
