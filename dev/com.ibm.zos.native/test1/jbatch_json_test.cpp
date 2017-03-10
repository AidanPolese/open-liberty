/*
  Copyright (c) 2009 Dave Gamble
 
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:
 
  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.
 
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
*/

#include <stdio.h>
#include <stdlib.h>
#include "../include/jbatch_json.h"
#include "include/CuTest.h"

/**
 *
 * -----------------------------------------------------------------------------
 * INSTRUCTIONS FOR CREATING AND RUNNING A NEW NATIVE TEST SUITE:
 * -----------------------------------------------------------------------------
 * See include/CuTest.h
 *
 *
 * Native unit tests for mvs_cell_pool_services.
 *
 */

extern void println(const char * message_p, ...);

// /* Parse text to JSON, then render back to text, and print! */
// void doit(CuTest * tc, char *text)
// {
// 	char *out;cJSON *json;
// 
//     println(__FUNCTION__ ": entry");
// 
//     void * m = malloc(10);
//     println(__FUNCTION__ ": malloc: %x", m);
// 	
// 	json=cJSON_Parse(text);
// 
// 	if (!json) {
//         printf("Error before: [%s]\n",cJSON_GetErrorPtr());
//         // -rx- CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), json);
//     }
// 	else
// 	{
// 		out=cJSON_Print(json);
// 		cJSON_Delete(json);
// 		printf("in:  %s\n",text);
// 		printf("out: %s\n",out);
// 		free(out);
// 	}
// }
// 
// /* Read a file, parse, render back, etc. */
// void dofile(CuTest * tc, char *filename)
// {
// 	FILE *f;long len;char *data;
// 	
// 	f=fopen(filename,"rb");fseek(f,0,SEEK_END);len=ftell(f);fseek(f,0,SEEK_SET);
// 	data=(char*)malloc(len+1);fread(data,1,len,f);fclose(f);
// 	doit(tc, data);
// 	free(data);
// }
// 
// /* Used by some code below as an example datatype. */
// struct record {const char *precision;double lat,lon;const char *address,*city,*state,*zip,*country; };
// 
// /* Create a bunch of objects as demonstration. */
// void create_objects()
// {
// 	cJSON *root,*fmt,*img,*thm,*fld;char *out;int i;	/* declare a few. */
// 	/* Our "days of the week" array: */
// 	const char *strings[7]={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
// 	/* Our matrix: */
// 	int numbers[3][3]={{0,-1,0},{1,0,0},{0,0,1}};
// 	/* Our "gallery" item: */
// 	int ids[4]={116,943,234,38793};
// 	/* Our array of "records": */
// 	struct record fields[2]={
// 		{"zip",37.7668,-1.223959e+2,"","SAN FRANCISCO","CA","94107","US"},
// 		{"zip",37.371991,-1.22026e+2,"","SUNNYVALE","CA","94085","US"}};
// 
// 	/* Here we construct some JSON standards, from the JSON site. */
// 	
// 	/* Our "Video" datatype: */
// 	root=cJSON_CreateObject();	
// 	cJSON_AddItemToObject(root, "name", cJSON_CreateString("Jack (\"Bee\") Nimble"));
// 	cJSON_AddItemToObject(root, "format", fmt=cJSON_CreateObject());
// 	cJSON_AddStringToObject(fmt,"type",		"rect");
// 	cJSON_AddNumberToObject(fmt,"width",		1920);
// 	cJSON_AddNumberToObject(fmt,"height",		1080);
// 	cJSON_AddFalseToObject (fmt,"interlace");
// 	cJSON_AddNumberToObject(fmt,"frame rate",	24);
// 	
// 	out=cJSON_Print(root);	cJSON_Delete(root);	printf("%s\n",out);	free(out);	/* Print to text, Delete the cJSON, print it, release the string. */
// 
// 	/* Our "days of the week" array: */
// 	root=cJSON_CreateStringArray(strings,7);
// 
// 	out=cJSON_Print(root);	cJSON_Delete(root);	printf("%s\n",out);	free(out);
// 
// 	/* Our matrix: */
// 	root=cJSON_CreateArray();
// 	for (i=0;i<3;i++) cJSON_AddItemToArray(root,cJSON_CreateIntArray(numbers[i],3));
// 
// /*	cJSON_ReplaceItemInArray(root,1,cJSON_CreateString("Replacement")); */
// 	
// 	out=cJSON_Print(root);	cJSON_Delete(root);	printf("%s\n",out);	free(out);
// 
// 
// 	/* Our "gallery" item: */
// 	root=cJSON_CreateObject();
// 	cJSON_AddItemToObject(root, "Image", img=cJSON_CreateObject());
// 	cJSON_AddNumberToObject(img,"Width",800);
// 	cJSON_AddNumberToObject(img,"Height",600);
// 	cJSON_AddStringToObject(img,"Title","View from 15th Floor");
// 	cJSON_AddItemToObject(img, "Thumbnail", thm=cJSON_CreateObject());
// 	cJSON_AddStringToObject(thm, "Url", "http:/*www.example.com/image/481989943");
// 	cJSON_AddNumberToObject(thm,"Height",125);
// 	cJSON_AddStringToObject(thm,"Width","100");
// 	cJSON_AddItemToObject(img,"IDs", cJSON_CreateIntArray(ids,4));
// 
// 	out=cJSON_Print(root);	cJSON_Delete(root);	printf("%s\n",out);	free(out);
// 
// 	/* Our array of "records": */
// 
// 	root=cJSON_CreateArray();
// 	for (i=0;i<2;i++)
// 	{
// 		cJSON_AddItemToArray(root,fld=cJSON_CreateObject());
// 		cJSON_AddStringToObject(fld, "precision", fields[i].precision);
// 		cJSON_AddNumberToObject(fld, "Latitude", fields[i].lat);
// 		cJSON_AddNumberToObject(fld, "Longitude", fields[i].lon);
// 		cJSON_AddStringToObject(fld, "Address", fields[i].address);
// 		cJSON_AddStringToObject(fld, "City", fields[i].city);
// 		cJSON_AddStringToObject(fld, "State", fields[i].state);
// 		cJSON_AddStringToObject(fld, "Zip", fields[i].zip);
// 		cJSON_AddStringToObject(fld, "Country", fields[i].country);
// 	}
// 	
// /*	cJSON_ReplaceItemInObject(cJSON_GetArrayItem(root,1),"City",cJSON_CreateIntArray(ids,4)); */
// 	
// 	out=cJSON_Print(root);	cJSON_Delete(root);	printf("%s\n",out);	free(out);
// 
// }

/**
 *
 */
void test_basicJsonObject(CuTest * tc)
{

	char text1[]="{\n\"name\": \"Jack (\\\"Bee\\\") Nimble\", \n\"format\": {\"type\":       \"rect\", \n\"width\":      1920, \n\"height\":     1080, \n\"interlace\":  false,\"frame rate\": 24\n}\n}";	

    println(__FUNCTION__ ": entry");

	cJSON* root=cJSON_Parse(text1);
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), root);

    cJSON* item = cJSON_GetObjectItem(root,"name");
    CuAssertStrEquals(tc, "name", item->string);
    CuAssertStrEquals(tc, "Jack (\"Bee\") Nimble", item->valuestring);

    cJSON_Delete(root);
}

/**
 *
 */
void test_basicJsonArray(CuTest * tc)
{

	char text2[]="[\"Sunday\",\"Monday\",\"Tuesday\",\"Wednesday\",\"Thursday\",\"Friday\",\"Saturday\"]";

    println(__FUNCTION__ ": entry");

	cJSON* head=cJSON_Parse(text2);
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), head);

    cJSON* item = cJSON_GetArrayItem(head,2);
    CuAssertStrEquals(tc, "Tuesday", item->valuestring);
    item = item->next;
    CuAssertStrEquals(tc, "Wednesday", item->valuestring);

    cJSON_Delete(head);
}

/**
 *
 */
void test_subArrays(CuTest * tc)
{

	char text3[]="[\n    [0, -1, 0],\n    [1, 0, 0],\n    [0, 0, 1]\n	]\n";

    println(__FUNCTION__ ": entry");

	cJSON* head=cJSON_Parse(text3);
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), head);

    cJSON* item = cJSON_GetArrayItem(head,1);
    CuAssertIntEquals(tc, 1, cJSON_GetArrayItem(item,0)->valueint);
    CuAssertIntEquals(tc, 0, cJSON_GetArrayItem(item,1)->valueint);
    CuAssertIntEquals(tc, 0, cJSON_GetArrayItem(item,2)->valueint);

    item = item->next;
    CuAssertIntEquals(tc, 0, cJSON_GetArrayItem(item,0)->valueint);
    CuAssertIntEquals(tc, 0, cJSON_GetArrayItem(item,1)->valueint);
    CuAssertIntEquals(tc, 1, cJSON_GetArrayItem(item,2)->valueint);

    CuAssertPtrIsNull(tc,item->next);
    cJSON_Delete(head);
}

/**
 *
 */
void test_childJsonObject(CuTest * tc)
{

	char text4[]="{\n		\"Image\": {\n			\"Width\":  800,\n			\"Height\": 600,\n			\"Title\":  \"View from 15th Floor\",\n			\"Thumbnail\": {\n				\"Url\":    \"http:/*www.example.com/image/481989943\",\n				\"Height\": 125,\n				\"Width\":  \"100\"\n			},\n			\"IDs\": [116, 943, 234, 38793]\n		}\n	}";

    println(__FUNCTION__ ": entry");

	cJSON* root=cJSON_Parse(text4);
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), root);

    cJSON* item = cJSON_GetObjectItem(root,"Image");
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), item);

    item = cJSON_GetObjectItem(item,"Thumbnail");
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), item);

    item = cJSON_GetObjectItem(item,"Url");
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), item);

    CuAssertStrEquals(tc, "Url", item->string);
    CuAssertStrEquals(tc, "http:/*www.example.com/image/481989943", item->valuestring);

    item = item->next;
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), item);

    CuAssertStrEquals(tc, "Height", item->string);
    CuAssertIntEquals(tc, 125, item->valueint);

    cJSON_Delete(root);
}

/**
 *
 */
void test_parseDouble(CuTest * tc)
{

	char text5[]="[\n	 {\n	 \"precision\": \"zip\",\n	 \"Latitude\":  37.7668,\n	 \"Longitude\": -122.3959,\n	 \"Address\":   \"\",\n	 \"City\":      \"SAN FRANCISCO\",\n	 \"State\":     \"CA\",\n	 \"Zip\":       \"94107\",\n	 \"Country\":   \"US\"\n	 },\n	 {\n	 \"precision\": \"zip\",\n	 \"Latitude\":  37.371991,\n	 \"Longitude\": -122.026020,\n	 \"Address\":   \"\",\n	 \"City\":      \"SUNNYVALE\",\n	 \"State\":     \"CA\",\n	 \"Zip\":       \"94085\",\n	 \"Country\":   \"US\"\n	 }\n	 ]";

    println(__FUNCTION__ ": entry");

	cJSON* head=cJSON_Parse(text5);
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), head);

    cJSON* item = cJSON_GetArrayItem(head,0);
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), item);

    item = cJSON_GetObjectItem(item,"Latitude");
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), item);

    CuAssertStrEquals(tc, "Latitude", item->string);
    CuAssertDblEquals(tc, 37.7668, item->valuedouble, 0.0001);

    cJSON_Delete(head);
}

/**
 *
 */
void test_createStringArray(CuTest * tc) {

	/* Our "days of the week" array: */
	const char *strings[7]={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

    println(__FUNCTION__ ": entry");

	cJSON * root=cJSON_CreateStringArray(strings,7);
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), root);

    int i=0;
    for ( cJSON* item = cJSON_GetArrayItem(root,0);
          item != NULL;
          item = item->next, ++i) {

        CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), item);
        CuAssertStrEquals(tc, strings[i], item->valuestring);
    }

    CuAssertIntEquals(tc, 7, i);

    cJSON_Delete(root);
}

/**
 *
 */
void test_createObject(CuTest * tc) {

    println(__FUNCTION__ ": entry");

	/* Our "Video" datatype: */
	cJSON * root=cJSON_CreateObject();	
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), root);

	cJSON_AddItemToObject(root, "name", cJSON_CreateString("Jack (\"Bee\") Nimble"));

    cJSON * fmt = cJSON_CreateObject();
	cJSON_AddItemToObject(root, "format", fmt);
	cJSON_AddStringToObject(fmt,"type",		"rect");
	cJSON_AddNumberToObject(fmt,"width",		1920);
	cJSON_AddNumberToObject(fmt,"height",		1080);
	cJSON_AddFalseToObject (fmt,"interlace");
	cJSON_AddNumberToObject(fmt,"frame rate",	24);

    cJSON* item = cJSON_GetObjectItem(root,"name");
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), item);

    CuAssertStrEquals(tc, "name", item->string);
    CuAssertStrEquals(tc, "Jack (\"Bee\") Nimble", item->valuestring);

    item = cJSON_GetObjectItem(root,"format");
    item = cJSON_GetObjectItem(item,"type");
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), item);

    CuAssertStrEquals(tc, "type", item->string);
    CuAssertStrEquals(tc, "rect", item->valuestring);

    cJSON_Delete(root);
}

/**
 *
 */
void test_addItemToArray(CuTest * tc) {

    println(__FUNCTION__ ": entry");

	int numbers[3][3]={{0,-1,0},{1,0,0},{0,0,1}};

	/* Our matrix: */
	cJSON * root=cJSON_CreateArray();
	for (int i=0;i<3;i++) {
        cJSON_AddItemToArray(root,cJSON_CreateIntArray(numbers[i],3));
    }

    cJSON* item = cJSON_GetArrayItem(root,1);
    CuAssertIntEquals(tc, 1, cJSON_GetArrayItem(item,0)->valueint);
    CuAssertIntEquals(tc, 0, cJSON_GetArrayItem(item,1)->valueint);
    CuAssertIntEquals(tc, 0, cJSON_GetArrayItem(item,2)->valueint);

    item = item->next;
    CuAssertIntEquals(tc, 0, cJSON_GetArrayItem(item,0)->valueint);
    CuAssertIntEquals(tc, 0, cJSON_GetArrayItem(item,1)->valueint);
    CuAssertIntEquals(tc, 1, cJSON_GetArrayItem(item,2)->valueint);

    CuAssertPtrIsNull(tc,item->next);

    cJSON_Delete(root);
}

/**
 * ...
 */
void test_getObjectItemIntValue(CuTest * tc) {

    println(__FUNCTION__ ": entry");

	cJSON * root=cJSON_CreateObject();	
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), root);

	cJSON_AddStringToObject(root,"s","11");
	cJSON_AddStringToObject(root,"t","blah");
	cJSON_AddNumberToObject(root,"i",19);

    CuAssertIntEquals(tc, 11, cJSON_GetObjectItemIntValue(root, "s", -1) );
    CuAssertIntEquals(tc, 0, cJSON_GetObjectItemIntValue(root, "t", -1) );
    CuAssertIntEquals(tc, 19, cJSON_GetObjectItemIntValue(root, "i", -1) );
    CuAssertIntEquals(tc, -1, cJSON_GetObjectItemIntValue(root, "x", -1) );

    cJSON_Delete(root);
}

/**
 * ...
 */
void test_mergeObjects(CuTest * tc) {

    println(__FUNCTION__ ": entry");

    // src obj.
	cJSON * src=cJSON_CreateObject();	
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), src);

	cJSON_AddStringToObject(src,"s","11");
	cJSON_AddStringToObject(src,"t","blah");
	cJSON_AddNumberToObject(src,"i",19);

    // dest obj.
	cJSON * dest=cJSON_CreateObject();	
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), dest);

	cJSON_AddStringToObject(dest,"s","10"); // will be overwritten

    cJSON_MergeObjects(dest, src, 1);

    CuAssertStrEquals(tc, "11", cJSON_GetObjectItemStringValue(dest, "s", NULL) );
    CuAssertStrEquals(tc, "blah", cJSON_GetObjectItemStringValue(dest, "t", NULL) );
    CuAssertIntEquals(tc, 19, cJSON_GetObjectItemIntValue(dest, "i", -1) );

    // dest obj.
	cJSON * dest2=cJSON_CreateObject();	
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), dest2);

	cJSON_AddStringToObject(dest2,"s","10"); // will NOT be overwritten

    cJSON_MergeObjects(dest2, src, 0);

    CuAssertStrEquals(tc, "10", cJSON_GetObjectItemStringValue(dest2, "s", NULL) );
    CuAssertStrEquals(tc, "blah", cJSON_GetObjectItemStringValue(dest2, "t", NULL) );
    CuAssertIntEquals(tc, 19, cJSON_GetObjectItemIntValue(dest2, "i", -1) );

    cJSON_Delete(src);
    cJSON_Delete(dest);
    cJSON_Delete(dest2);
}


/**
 *
 * Test that control chars ("\"\\\b\f\n\r\t") are properly escaped.
 */
void test_charEscaping(CuTest * tc) {

    println(__FUNCTION__ ": entry");

	cJSON * root=cJSON_CreateObject();	
    CuAssertPtrNotNullMsg(tc, cJSON_GetErrorPtr(), root);


	cJSON_AddItemToObject(root, "s1", cJSON_CreateString("hello \" goodbye"));
	cJSON_AddItemToObject(root, "s2", cJSON_CreateString("hello \\ goodbye"));
	cJSON_AddItemToObject(root, "s3", cJSON_CreateString("hello \b goodbye"));
	cJSON_AddItemToObject(root, "s4", cJSON_CreateString("hello \f goodbye"));
	cJSON_AddItemToObject(root, "s5", cJSON_CreateString("hello \n goodbye"));
	cJSON_AddItemToObject(root, "s6", cJSON_CreateString("hello \r goodbye"));
	cJSON_AddItemToObject(root, "s7", cJSON_CreateString("hello \t goodbye"));
	cJSON_AddItemToObject(root, "s8", cJSON_CreateString("hello \" \\ \b \f \n \r \t goodbye"));


    char * s = cJSON_Print( cJSON_GetObjectItem(root,"s1") );
    println(__FUNCTION__ ": %s", s);
    CuAssertStrEquals(tc, "\"hello \\\" goodbye\"", s);

    s = cJSON_Print( cJSON_GetObjectItem(root,"s2") );
    println(__FUNCTION__ ": %s", s);
	CuAssertStrEquals(tc, "\"hello \\\\ goodbye\"", s);

    s = cJSON_Print( cJSON_GetObjectItem(root,"s3") );
    println(__FUNCTION__ ": %s", s);
	CuAssertStrEquals(tc, "\"hello \\b goodbye\"", s);

    s = cJSON_Print( cJSON_GetObjectItem(root,"s4") );
    println(__FUNCTION__ ": %s", s);
	CuAssertStrEquals(tc, "\"hello \\f goodbye\"", s);

    s = cJSON_Print( cJSON_GetObjectItem(root,"s5") );
    println(__FUNCTION__ ": %s", s);
	CuAssertStrEquals(tc, "\"hello \\n goodbye\"", s);

    s = cJSON_Print( cJSON_GetObjectItem(root,"s6") );
    println(__FUNCTION__ ": %s", s);
	CuAssertStrEquals(tc, "\"hello \\r goodbye\"", s);

    s = cJSON_Print( cJSON_GetObjectItem(root,"s7") );
    println(__FUNCTION__ ": %s", s);
	CuAssertStrEquals(tc, "\"hello \\t goodbye\"", s);

    s = cJSON_Print( cJSON_GetObjectItem(root,"s8") );
    println(__FUNCTION__ ": %s", s);
	CuAssertStrEquals(tc, "\"hello \\\" \\\\ \\b \\f \\n \\r \\t goodbye\"", s);

    cJSON_Delete(root);
}



/*-------------------------------------------------------------------------*
 * main
 *-------------------------------------------------------------------------*/

CuSuite* jbatch_json_test_suite(void)
{
    CuSuite* suite = CuSuiteNew("jbatch_json_test");

    SUITE_ADD_TEST(suite, test_basicJsonObject);
    SUITE_ADD_TEST(suite, test_basicJsonArray);
    SUITE_ADD_TEST(suite, test_subArrays);
    SUITE_ADD_TEST(suite, test_childJsonObject);
    SUITE_ADD_TEST(suite, test_parseDouble);
    SUITE_ADD_TEST(suite, test_createStringArray);
    SUITE_ADD_TEST(suite, test_createObject);
    SUITE_ADD_TEST(suite, test_addItemToArray);
    SUITE_ADD_TEST(suite, test_getObjectItemIntValue);
    SUITE_ADD_TEST(suite, test_mergeObjects);
    SUITE_ADD_TEST(suite, test_charEscaping);

    return suite;
}
