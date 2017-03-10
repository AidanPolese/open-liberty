This edition exists to create the zip files used by the z/OS build.
The z/OS build will then conver the zip files into pax files with
the zOS specific native files.

The edition JARs produced are converted to zip files in custom-build.xml
The edition JARs are then removed as they are not shipped or used
by Installation Manager.

This edition will produce and convert to the following:
wlp-zos-kernel-${edition.version}.jar   ->   wlp-zos-kernel-${buildLabel}.zip
wlp-zos-runtime-${edition.version}.jar  ->   wlp-zos-core-${buildLabel}.zip
wlp-zos-all-${edition.version}.jar      ->   wlp-zos-all-${buildLabel}.zip

The produced wlp-zos-repo-${edition.version}.jar is only used to build
the embeddable zip, and is therefore removed before publishing.

This edition will also produce the following embeddable zips:
wlp-zos-full-${edition.version}.zip
wlp-embeddable-zos-${edition.version}.zip
wlp-embeddable-signed-config-zos-${edition.version}.zip 

All of these zip files (except wlp-zos-full) are placed in output/ which
are then uploaded to GSA and finally pulled down by the zOS packaging build.

It might be more efficient to convert these different zips directly to
profiles (which would skip the jar / unjar / zip steps), but this can be
done later.
