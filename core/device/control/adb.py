from adbutils import adb
from core.device.connection import Connection
import time


class AdbControl:
    def __init__(self, conn):
        self.serial = conn.serial
        self.adb = adb.device(self.serial)
        # runtime-selected display id for `input -d <display>` usage
        self.display_id = None

    def set_display_id(self, display_id):
        """Set the current display id to be used by subsequent input commands.

        This keeps the public `click`/`swipe`/`long_click` signatures unchanged
        while allowing callers to select a display at runtime by calling
        `Control.set_display_id()` (forwarded to this method).
        """
        self.display_id = display_id

    def _build_input_cmd(self, action_cmd: str) -> str:
        """Build the adb shell input command, injecting display id if set."""
        if self.display_id:
            return f"input -d {self.display_id} {action_cmd}"
        return f"input {action_cmd}"

    def click(self, x, y):
        start_t = time.time()
        cmd = self._build_input_cmd(f'tap {x} {y}')
        self.adb.shell(cmd)
        if time.time() - start_t < 0.05:
            time.sleep(0.05)

    def swipe(self, x1, y1, x2, y2, duration):
        duration = int(duration * 1000)
        cmd = self._build_input_cmd(f'swipe {x1} {y1} {x2} {y2} {duration}')
        self.adb.shell(cmd)

    def long_click(self, x, y, duration):
        duration = int(duration * 1000)
        cmd = self._build_input_cmd(f'swipe {x} {y} {x} {y} {duration}')
        self.adb.shell(cmd)
