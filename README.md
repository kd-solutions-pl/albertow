
# Albertow
Albertow is a project that allows creating independent virtual machines for immutable rootfs images and backups for a separate read-write/data directory

### Why?
 1. I need to control what happens in the rootfs filesystem and prefer to use Yocto instead of Docker.
 2. I need isolated VMs with services I use.
 3. I need simple backup solution for complex tools, like [Redmine](https://www.redmine.org/) and update them on-demand without worrying that I break something. So I came up with the idea to simply generate immutable software images and files acting as `rw-data` partitions that can be backed up.

## Build

### Minimal image without much useful functionality
```Bash
    kas checkout kas/albertow.yaml
    . openembedded-core/oe-init-build-env
    bitbake albertow-image
```
### Redmine image
```Bash
    kas checkout kas/redmine.yaml
    . openembedded-core/oe-init-build-env
    bitbake redmine-albertow-image
```
## Running images with QEMU
Use `meta-albertow/scripts/run-qemu` for booting a built image with a read-only
squashfs root filesystem and a persistent ext4 data disk mounted by the image
at `/data`.

The script expects:

```Bash
    meta-albertow/scripts/run-qemu ARCH IMAGE
```

Supported architectures are:

```
    aarch64
    x86-64
    KVM
```

For example, boot an aarch64 Redmine image:

```Bash
    meta-albertow/scripts/run-qemu aarch64 redmine-albertow-image
```

Run a native image with KVM acceleration:

```Bash
    meta-albertow/scripts/run-qemu kvm redmine-albertow-image
```

On an x86-64 host, `kvm` uses `MACHINE=qemux86-64`. On an aarch64 host, `kvm` uses `MACHINE=qemuarm64`

By default, the script looks for deploy artifacts in:

```Bash
    build/tmp/deploy/images/qemuarm64
    build/tmp/deploy/images/qemux86-64
```

For a fully functional QEMU boot, DEPLOY directory must provide the kernel and squashfs root filesystem.

Override the deploy directory when needed:

```Bash
    DEPLOY=/path/to/tmp/deploy/images/qemuarm64 meta-albertow/scripts/run-qemu aarch64 redmine-albertow-image
```

The persistent data image defaults to:

```Bash
    $HOME/$IMAGE.data.ext4
```

If it does not exist, the script creates it as ext4. Use `DATA_IMAGE` to keep the data partition beside the build:

```Bash
    DATA_IMAGE=./redmine-image.data.ext4 meta-albertow/scripts/run-qemu aarch64 redmine-albertow-image
```

Set the data image size when it is first created:

```Bash
    DATA_IMAGE=./redmine-image.data.ext4 DATA_SIZE=4G meta-albertow/scripts/run-qemu aarch64 redmine-albertow-image
```

Host port forwarding defaults:

```Bash
    SSH_PORT=2222
    HTTP_PORT=3000
    HOST_IP=0.0.0.0
```

Access Redmine from the host:

```
    https://localhost:3000
```
