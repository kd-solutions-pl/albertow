SUMMARY = "Git extension for versioning large files"
DESCRIPTION = "Git LFS replaces large files with text pointers inside Git, while storing file contents on a remote server."
HOMEPAGE = "https://git-lfs.com/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE.md;md5=58e90ef3218ad8dd56d2b5790d035be1"

SRC_URI = "git://github.com/git-lfs/git-lfs.git;protocol=https;nobranch=1;destsuffix=${GO_SRCURI_DESTSUFFIX}"
SRCREV = "b84b33847fe6458f36ef521534dc0eac953cb379"

GO_IMPORT = "github.com/git-lfs/git-lfs/v3"
GO_INSTALL = ""

inherit go-mod

RDEPENDS:${PN} += "git"

export GO111MODULE = "on"
export GOTOOLCHAIN = "local"

GO_EXTRA_LDFLAGS += "-X github.com/git-lfs/git-lfs/v3/config.GitCommit=${SRCREV}"

do_compile[network] = "1"

do_compile() {
    export TMPDIR="${GOTMPDIR}"
    cd ${B}/src/${GO_IMPORT}
    install -d ${B}/bin
    ${GO} build ${GOBUILDFLAGS} -o ${B}/bin/git-lfs ./git-lfs.go
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/bin/git-lfs ${D}${bindir}/git-lfs
}
