
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

### Admin user
Images include one administrative user. By default, the user is:

```
    user: admin
    password: admin
```

On first boot with a new `/data` disk, the default password hash is initialized in
`/data/security/shadow` It is possible to override the default user or initial password hash in `build/conf/site.conf` before building the image:

```Bash
    ADMIN_USER = "myadmin"
    ADMIN_USER_PASSWORD = "$y$j9T$IsqVnWdqA.B4zwuVSYpEq1$3lfGGWjnx7CkT3SpHNK8eeQrmMc3UcWQRgxXMLBODp0"
```
Generate password hash with:

```Bash
    mkpasswd -m yescrypt
```
If you don't have this command then install `whois` package (Debian / Ubuntu), like
```Bash
    sudo apt install whois
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

Only one QEMU instance may use a given `DATA_IMAGE` at a time. `run-qemu`
creates a lock next to the data image and refuses to start another instance
while the lock owner process is still running.

Set the data image size when it is first created:

```Bash
    DATA_IMAGE=./redmine-image.data.ext4 DATA_SIZE=4G meta-albertow/scripts/run-qemu aarch64 redmine-albertow-image
```

Run QEMU in the background and expose the serial login console over telnet:

```Bash
    meta-albertow/scripts/run-qemu --background --telnet-port 4321 kvm redmine-albertow-image
```

Connect to the login prompt:

```Bash
    telnet 127.0.0.1 4321
```

The telnet console listens on `127.0.0.1` by default. Use `--telnet-host` if it
must listen on another interface. Background mode writes a QEMU pidfile to:

```Bash
    $HOME/$IMAGE.$MACHINE.qemu.pid
```

### Run QEMU with a user systemd service
`meta-albertow/scripts/albertow-qemu@.service` starts `run-qemu` in background
mode and exposes the serial login console over telnet. Install it as a user
service:

```Bash
    mkdir -p ~/.config/systemd/user
    ln -sf "$PWD/meta-albertow/scripts/albertow-qemu@.service" \
        ~/.config/systemd/user/albertow-qemu@.service
    systemctl --user daemon-reload
```

The service instance name after `@` is passed to `run-qemu` as the image name.
For example:

```Bash
    albertow-qemu@redmine-albertow-image.service
```

starts:

```Bash
    meta-albertow/scripts/run-qemu kvm redmine-albertow-image
```

With `ALBERTOW_ARCH=kvm` on an x86-64 host, `run-qemu` uses
`MACHINE=qemux86-64`. By default, the required deploy artifacts are:

```Bash
    build/tmp/deploy/images/qemux86-64/bzImage-qemux86-64.bin
    build/tmp/deploy/images/qemux86-64/redmine-albertow-image-qemux86-64.rootfs.squashfs
```

To keep the kernel and rootfs elsewhere, set `DEPLOY` in the instance
environment file. The directory named by `DEPLOY` must contain:

```Bash
    bzImage-qemux86-64.bin
    redmine-albertow-image-qemux86-64.rootfs.squashfs
```

Create a per-instance environment file for `redmine-albertow-image`:

```Bash
    mkdir -p ~/.config/albertow
    cat > ~/.config/albertow/qemu-redmine-albertow-image.env <<'EOF'
ALBERTOW_ARCH=kvm
DEPLOY=/home/drabina/install/qemu-images/redmine
TELNET_PORT=4321
SSH_PORT=2222
HTTP_PORT=3000
DATA_IMAGE=/home/drabina/install/qemu-data/redmine-albertow-image.data.ext4
EOF
```

Use a different `TELNET_PORT`, `SSH_PORT`, `HTTP_PORT`, and `DATA_IMAGE` for
each concurrently running QEMU instance.

Start the Redmine image:

```Bash
    systemctl --user start albertow-qemu@redmine-albertow-image.service
```

Connect to the login prompt:

```Bash
    telnet 127.0.0.1 4321
```

Check status and logs:

```Bash
    systemctl --user status albertow-qemu@redmine-albertow-image.service
    journalctl --user -u albertow-qemu@redmine-albertow-image.service
```

Stop QEMU safely through systemd:

```Bash
    systemctl --user stop albertow-qemu@redmine-albertow-image.service
```

After stopping, confirm that the service is inactive before reusing the same
data image:

```Bash
    systemctl --user is-active albertow-qemu@redmine-albertow-image.service
```

To start this QEMU instance automatically at host boot without logging in, enable
linger for your user and enable the user service:

```Bash
    sudo loginctl enable-linger "$USER"
    systemctl --user enable albertow-qemu@redmine-albertow-image.service
```

Start it immediately as well, if it is not already running:

```Bash
    systemctl --user start albertow-qemu@redmine-albertow-image.service
```

After reboot, check that systemd started the instance:

```Bash
    systemctl --user status albertow-qemu@redmine-albertow-image.service
    telnet 127.0.0.1 4321
```

Disable automatic boot start later with:

```Bash
    systemctl --user disable albertow-qemu@redmine-albertow-image.service
    sudo loginctl disable-linger "$USER"
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
