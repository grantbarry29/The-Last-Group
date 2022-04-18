import os

path = "/home/ubuntu/Car-Recognition/data/valid/"
new_path = "/home/ubuntu/The-Last-Group/cars/media/"

for i in range(0, 192):
    dir = path + str(i+1).zfill(4)
    list = os.listdir(dir)
    new_dir = new_path + str(i+1).zfill(4)+".jpg"
    os.rename(dir + "/" + list[0], new_dir)
