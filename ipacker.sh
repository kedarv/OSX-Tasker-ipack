#!/bin/bash

# v1.2
# generate small icons for notifications

# v1.1
# added icon select background colour arg
# generating screenshots, ignore dirs

mypath=$0
sdkdir="/Users/kedar/Documents/sdk"
version="1"
packagename="me.kedarv.ipack.OSX"
label="OSX"
allsamesize="true"
backcolour="00FFFFFF"
attribution="kedar"
icondir=$8
keystore=""
keyname=""

smalliconaffix="_"
androidiconsize="25x25"
startdir=`pwd`
scriptdir=`dirname mypath`
cd $scriptdir

if [ "$sdkdir" == "" ]; then
	echo "no SDK dir"
	exit 1
elif [ "$packagename" == "" ]; then
	echo "no package name"
	exit 1
elif [ "$attribution" == "" ]; then
	echo "no attribution"
	exit 1
elif [ "$allsamesize" != "true" ] && [ "$allsamesize" != "false" ]; then
	echo "allsamesize not 'true' or 'false'"
	exit 1
elif [ "$version" == "" ]; then
	echo "no version"
	exit 1
elif [ "$label" == "" ]; then
	echo "no label"
	exit 1
elif [ "$icondir" == "" ]; then
	echo "no icon dir"
	exit 1
elif [ "$keystore" == "" ]; then
	echo "no key store"
	exit 1
fi

if [ ! -d $sdkdir ]; then
	echo "$sdkdir: not exist"
	exit 1;
fi
if [ ! -d "$icondir" ]; then
	echo "$icondir: not exist"
	exit 1;
fi
if [ ! -f $keystore ]; then
	echo "$keystore: not exist"
	exit 1;
fi

buildbase=/tmp/icons

rm -rf $buildbase

echo "install files..."

srcpath=`echo $packagename |sed 's/\./\//g'` 
srcpath=src/$srcpath
shortlabel=`echo $label | sed 's/ //g'`

mkdir -p $buildbase/$srcpath
mkdir -p $buildbase/bin
mkdir -p $buildbase/gen
cp -a res $buildbase

safesdkdir=`echo $sdkdir | sed 's/\//\\\\\\//g'`
safekeystore=`echo $keystore | sed 's/\//\\\\\\//g'`

echo "build icon data..."

dpath="$buildbase/$srcpath/IpackContent.java"

echo -e "package $packagename;\nimport android.content.res.Resources;\nimport android.os.Bundle;\n\npublic class IpackContent {\n\n public final static boolean ALL_SAME_SIZE = $allsamesize;\npublic final static String LABEL = \"$label\";\npublic final static String ATTRIBUTION = \"$attribution\";\n\n\npublic static void fillBundle( Resources res, Bundle b ) {\n\n" > $dpath

drawabledir=drawable

for path in $icondir/*; do
	file=`basename "$path" .png`
	if [ -d "$path" ]; then
		echo "skipping $path, is a directory"
	elif [[ "$file" =~ ^[0-9] ]]; then
		echo "skipping $file, starts with number"
	elif [ "$file" == "package" ]; then
		echo "skipping $file, restricted keyword"
	elif [[ "$file" =~ ó ]]; then
		echo "skipping $file, non-ASCII"
	elif [[ "$file" =~ ^[A-Za-z\ 0-9_\-]*$ ]]; then
		file=`echo "$file" | tr A-Z a-z | sed 's/[^a-z0-9]/_/g'`
		outpath="$buildbase/res/$drawabledir/$file.png"
		cp "$path" "$outpath"

		# make small icon for notifications
		smalloutpath="$buildbase/res/$drawabledir/$file$smalliconaffix.png"
		convert "$outpath" -resize $androidiconsize "$smalloutpath"

		name=`basename $file png`
		newone=`echo -e "b.putInt( \"$name\", R.drawable.$name );\n"`
		echo $newone >> "$dpath"
	else
		echo "skipping $file, bad format"
	fi
done

echo -e "\n}\n}" >> $dpath

cat AndroidManifest.xml | sed "s/%VERSION%/$version/g" | sed "s/%PACKAGE_NAME%/$packagename/g" | sed "s/%LABEL%/$label/g" > $buildbase/AndroidManifest.xml
cat build.xml | sed "s/%LABEL%/$shortlabel/g" > $buildbase/build.xml
cat local.properties | sed "s/%KEY_NAME%/$keyname/g" | sed "s/%KEY_STORE%/$safekeystore/g"  | sed "s/%SDK_DIR%/$safesdkdir/g" >  $buildbase/local.properties
cat IpackKeys.java | sed "s/%PACKAGE_NAME%/$packagename/g"  > $buildbase/$srcpath/IpackKeys.java
cat IpackReceiver.java | sed "s/%PACKAGE_NAME%/$packagename/g" > $buildbase/$srcpath/IpackReceiver.java
cat IpackIconSelect.java | sed "s/%PACKAGE_NAME%/$packagename/g" | sed "s/%BACK_COLOUR%/$backcolour/g" > $buildbase/$srcpath/IpackIconSelect.java

cd $buildbase

ant clean release


if [ "$?" == 0 ]; then

	if [ -d $startdir/built ]; then
		putdir=$startdir/built
	elif [ -d $startdir/../built ]; then
		putdir=$startdir/../built
	else
		putdir=$startdir
	fi
	put=$putdir/$shortlabel.apk

	mv bin/$shortlabel-release.apk "$put"
	cd $startdir
	ls -l $put

	echo "Install APK..."
	adb install -r $put
	
	montagepath=`which montage`

	if [ "$montagepath" == "" ]; then
		echo "montage missing: maybe install image-magick to generate screenshot gallery ?"
	else
		echo "Generating screenshots..."
		montage -depth 24 -resize 48x48 -format jpg -background white -geometry +16+16 -tile 4x6 `find $icondir -type f | grep -v " "` $putdir/$shortlabel.screen.jpg
	fi


	rm -rf $buildbase

	echo Done.
else
	rm -rf $buildbase
	exit 1
fi


