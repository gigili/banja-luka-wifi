import 'package:appwrite/appwrite.dart';

const String projectID = "63be850964c9ab849fc8";
const String collectionID = "63bd648ea223dd87fcbd";
const String databaseID = "63bd648905e3d5921b6f";

Client get client => Client(endPoint: "https://appwrite.igorilic.dev/v1").setProject(projectID);

Databases get databases => Databases(client);

const String developerName = 'Igor Ilić';
const String developerEmail = 'github@igorilic.net';
const String developerWebsite = 'https://igorilic.net';
const String developerSourceCode = 'https://github.com/gigili/banja-luka-wifi';
