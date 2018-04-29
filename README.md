# AnsibleVault

This tool is designed to give to windows and mac users the same `ansible-vault` capability
available to linux users. It is written in Java and requires the unlimited jce policy files
and at least Java 1.8.

To install this distribution simply extract the zip file into a directory of your choosing
and ensure that directory is in your PATH.

There are a few small differences between this tool and the one shipped with ansible. The
biggest being that it includes its own editor rather than using your system configured
editor. There are no temporary files containing decrypted plaintext.

Current Release Version: 1.0


Creating Encrypted Files
------------------------

To create a new encrypted data file, run the following command:

```
ansible-vault create foo.yml
```

First you will be prompted for a password. After providing a password, the tool will
launch its built in editor. Once you are done with the editor session, the data will be
saved as an encrypted vault file.


Editing Encrypted Files
-----------------------

To edit an encrypted file in place, use the ansible-vault edit command. This command will
decrypt the file and allow you to edit the file, encrypting and saving it back when done:

```
ansible-vault edit foo.yml
```

The contents of the file are never written to disk in unencrypted form.


Rekeying Encrypted Files
------------------------

Should you wish to change your password on a vault-encrypted file or files, you can do so
with the rekey command:

```
ansible-vault rekey foo.yml bar.yml baz.yml
```

This command can rekey multiple data files at once and will ask for the original password
and also the new password.

When operating on multiple files it is assumed that all the files are encrypted using a
single password key. Files that cannot be verified using that key will be skipped.


Encrypting Unencrypted Files
----------------------------

If you have existing files that you wish to encrypt, use the ansible-vault encrypt command.
This command can operate on multiple files at once:

```
ansible-vault encrypt foo.yml bar.yml baz.yml
```

When operating on multiple files it is assumed that all the files are encrypted using a
single password key.



Decrypting Unencrypted Files
----------------------------

If you have existing files that you no longer want to keep encrypted, you can permanently
decrypt them by running the ansible-vault decrypt command. This command will save them
unencrypted to the disk, so be sure you do not want ansible-vault edit instead:

```
ansible-vault decrypt foo.yml bar.yml baz.yml
```

When operating on multiple files it is assumed that all the files are encrypted using a
single password key. Files that cannot be verified using that key will be skipped.



Viewing Encrypted Files
-----------------------

If you want to view the contents of an encrypted file without editing it, you can use the
ansible-vault view command:

```
ansible-vault view foo.yml bar.yml baz.yml
```

When operating on multiple files it is assumed that all the files are encrypted using a
single password key. Files that cannot be verified using that key will be skipped.

