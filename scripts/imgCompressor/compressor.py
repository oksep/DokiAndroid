# -*- coding: utf-8 -*-

import sys
import os
import argparse
import subprocess

pngQuantPath = os.path.join(os.path.dirname(os.path.abspath(__file__)), "bin/pngquant-openmp")

def printHelp():
  Print.msg('Help:')
  Print.msg(u'\ndo compress: \n')
  Print.msg(u'\tpython compress.py compress [folderOrFile]')

fileSize = lambda file : os.stat(file).st_size / 1024.0

def log(file, before, after):
  logStr = '%f%% compressed %s, before %f after %f' % ((before - after) / before * 100, file, before, after)
  Print.msg(logStr)

def imageFind(folder):
  for folder, subs, files in os.walk(folder):
    for f in files:
      if f.endswith('png') or f.endswith('jpg') or f.endswith('jpeg'):
        yield os.path.join(folder, f)

def doCompress(f):
  file = os.path.abspath(f)
  try:
    originFileSize = fileSize(file)
    outputfile = file + '.bk'
    subprocess.call([pngQuantPath, file, "-f", "--output", outputfile])#, stdout=subprocess.PIPE).stdout
    afterFileSize = fileSize(outputfile)
    if afterFileSize >= originFileSize:
      subprocess.call(['rm', outputfile])#, stdout=subprocess.PIPE).stdout
      return 0,0,0
    log(file, originFileSize, afterFileSize)
    subprocess.call(['mv', outputfile, file])#, stdout=subprocess.PIPE).stdout
    return originFileSize, afterFileSize, originFileSize -  afterFileSize
  except Exception as e:
    Print.fail(e)
    log(file, -1, -1)
    return 0, 0, 0

import operator
def compress(args):
  fileOrFolder = args[0]
  ot, at, st = 0, 0, 0
  if os.path.isfile(fileOrFolder):
    ot, at, st = tuple(map(operator.add, (ot,at,st), doCompress(fileOrFolder)))
  else:
    for file in imageFind(fileOrFolder):
      ot, at, st = tuple(map(operator.add, (ot,at,st), doCompress(file)))
  Print.ok('\n\nInfo: \norigin total: \t%dkb \nafter total: \t%dkb \nshrink total: \t%dkb' % (ot, at, st))

class Print(object):
  @staticmethod
  def msg(m): 
    print '\033[1;37m' + m + '\033[1;m'
  
  @staticmethod
  def ok(m):
    print '\033[1;32m' + m + '\033[1;m\n'
  
  @staticmethod
  def fail(m):
    print '\033[1;31m' + m + '\033[1;m\n'

def checkPngQuant():
  Print.msg('checking compressor tool: pngquant')
  if os.access(pngQuantPath, os.X_OK):
    Print.ok('ok')
    return True
  else:
    Print.fail('not executable, you can use this command to enable it:')
    Print.msg('chmod +x ' + pngQuantPath + "\n")
    return False

def buildExist(fileOrFolder):
  if os.path.isfile(fileOrFolder):
    return False
  for folder, subs, files in os.walk(fileOrFolder):
    for f in subs:
      if ('build' in subs):
        return True
  return False

def checkIfBuildFolderExist(folder):
  Print.msg('Checking if build folder exists')
  if buildExist(folder):
    name = raw_input("\033[1;37mBuild folder detected, proceed anyway? (N/y)\033[1;m\n")
    if name.strip() == '' or name.lower().strip() == 'n':
      Print.msg("Please clean your Android project first in case to do useless compressions")
      exit(0)
  else:
    Print.ok("no build folder detected")

if __name__ == '__main__':
  if len(sys.argv) < 2:
    printHelp()
  elif sys.argv[1] == 'compress':
    if len(sys.argv) < 3:
      Print.fail('please provide the target file or folder')
      exit(0)
    if checkPngQuant() is False: 
      exit(0)
    checkIfBuildFolderExist(sys.argv[2])
    Print.msg("start to compress")
    compress(sys.argv[2:])