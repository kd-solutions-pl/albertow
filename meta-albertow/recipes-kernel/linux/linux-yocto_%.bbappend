FILESEXTRAPATHS:prepend := "${THISDIR}/linux-yocto:"

SRC_URI:append = " file://squashfs-rootfs.cfg"

KMACHINE:albertow-qemuarm64 = "qemuarm64"
KMACHINE:albertow-qemux86-64 = "qemux86-64"
