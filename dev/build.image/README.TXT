This project is responsible for building the archive installers. The main entry is in the build-image.xml.

The output in this generated into the output/upload/externals/installables directory. These files are uploaded
and listed in the fe output xml files. 

The editions directory contains one directory per edition. It also contains some special ones. In each editions there
is a runtime.xml and runtime.properties which defines that editions runtime archive. It can also include multiple xml
files for each additional archive and an embeddable.properties that will create an embedders archive.

Currently we have the following product editions:

base
core
core-isv
developers (ILAN)
developers (IPLA) (we use developers-ipla for this one)
express
nd
zos         - this is special in that we generate something for use by the z build, rather than this being shippable

We also have these non-product editions which we ship:

beta - the beta archive
bluemix - used by CloudOE/Bluemix

And these functional ones:

core-noship     - used by IM
full            - used by IM
unsupported     - used to generate unsupported esas.

Adding a new edition
To add a new edition you just create a new directory in the editions folder. The only requirement is it contains a
runtime.xml and runtime.properties file and a runtime/lafiles directory containing the licenses. If mvt is required then
an mvt.txt file should be checked in.

If addon archives (like the extensions zip) are required then you create a <addon>.xml <addon>.properties and 
<addon>/lafiles directory. The <addons>.xml tells us the content and name of the archive, the properties configures
how it is build and the <addon>/lafiles includes the licenses.

If an embeddable.properties is defined then the runtime and all addons will be installed and a zip will be created
with the license and swg tags removed.

runtime.properties
The following properties affect how the runtime archive is created:
license.type        - the type of license. There are a few like IPLA (supported), 
                      ILAN (unsupported), ILAE (trial), and ILAR (alpha/beta). We also
                      use NOSHIP in some cases where we don't want something shipped.
product.edition     - The edition of the product, this is typically something like LIBERTY_CORE or BASE
add.to.download.xml - If set to true this will be added to the download.xml used for WASdev.
dhe.license.path    - Only relevant if add.to.download.xml is set. This defines where on DHE the licenses will
                      be stored so they can be looked up by WASdev.
build.always        - If set to true then it will be built in personal and local builds. If not set it'll only
                      be built in the Release build.
archive.name        - Can be used to override the default archive name which is  wlp-<edition>-runtime-<version>.properties
build.repo.metadata - Set to true if a metadata zip should be built to supply information to the repository 
                      (used for all artifacts that should be loaded into the repostiory)
edition.version     - The version of this edition, typically will be either the current service level or the current beta
                      level
display.edition.version - Optional. A user friendly version of the edition version, currently just used for the "beta"
                      instead of displaying the machine version of yyyy.mm.0.0.
build.profile.zip   - Set in Release Build definition. Controls whether or not the profiles which
                      bundle Java are packaged
repo.x              - A property that will be put into the repository metadata properties file, the "repo." will 
                      be stripped

These properties are also valid in the addons properties files and have the same behaviour.

<addons.properties>
build.addon.esa     - Set to true to build an ESA that requires the features for this addon
editions            - The list of editions this can be installed onto. e.g. BASE,DEVELOPERS,EXPRESS,ND
addon.esa.editions  - The list of editions this can be installed onto when being installed through the ESA, only relevant
                      when build.addon.esa is true. This is optional and defaults to editions if not supplied.

embeddable.properties
build.embeddable    - If this is set to true we will create an embeddable by installing all the archives for this edition
extra.archives      - Specifies to install any additional archives. This is used for the base, express and nd embeddables
                      where they depend on the extensions archive built by the base edition.
archive.name        - Can be used to override the default archive name which is wlp-<edition>-embeddable-<version>.zip
delete.source       - If set to true the runtime jar file will be removed once the embedded archive is created

Power feature
If you add a custom-build.xml with a customize target that target will be run for the edition. This is run after
everything else is built. This should NOT be used without a compelling reason. It was required for the zos image
because they do some crazy things for z/OSMF.

TODO

There are a few odds and ends that will need to be tidied up, the mvt and archives directories should be retired
and we need to mvt the extensions zips. Also the extras archives should be lost and we want to remove a few
straggling items from the image.common folder, like the html licenses.


Server.xml Variables: ${editions.*} and  ${profiles.*}
------------------------------------------------------
These variables are established by the prepareForPackaging target in build-image.xml.
This target maps all of the existing editions/ and profiles/ directories to properties
defined in a bootstrap.properties file the image server uses. The source bootstrap.properties
file is in resources/imageServer/ 