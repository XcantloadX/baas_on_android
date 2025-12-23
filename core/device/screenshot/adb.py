from adbutils import adb
import cv2
import numpy as np


class AdbScreenshot:
    def __init__(self, conn):
        self.serial = conn.serial
        self.logger = conn.logger

        self.adb = adb.device(self.serial)
        self.display_id = None

    def set_display_id(self, display_id):
        self.display_id = display_id

    def screenshot(self):
        # Build screencap command; inject display id if provided.
        if self.display_id:
            # many adb/screencap variants accept `-d` for display; keep format simple
            cmd = ['screencap', '-p', '-d', str(self.display_id)]
        else:
            cmd = ['screencap', '-p']

        data = self.adb.shell(cmd, stream=False, encoding=None)
        if len(data) < 500:
            self.logger.warning(f'Unexpected screenshot: {data}')
        image = np.frombuffer(data, np.uint8)
        image = cv2.imdecode(image, cv2.IMREAD_COLOR)
        return image

