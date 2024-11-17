import 'package:banjalukawifi/l10n/l10n.dart';
import 'package:banjalukawifi/utility/constants.dart';
import 'package:flutter/material.dart';
import 'package:flutter_widget_from_html/flutter_widget_from_html.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:url_launcher/url_launcher.dart';

class AppInfoScreen extends StatefulWidget {
  const AppInfoScreen({super.key});

  @override
  State<AppInfoScreen> createState() => _AppInfoScreenState();
}

class _AppInfoScreenState extends State<AppInfoScreen> {
  Map<String, dynamic> appData = <String, dynamic>{};

  @override
  void didChangeDependencies() {
    setupAppInfo();
    super.didChangeDependencies();
  }

  @override
  Widget build(BuildContext context) {
    return ListView.separated(
      itemCount: appData.length,
      separatorBuilder: (_, __) => const Divider(thickness: 1),
      itemBuilder: (ctx, index) {
        var key = appData.keys.elementAt(index);
        dynamic item = appData[key];

        if (item is String) {
          return ListTile(
            title: Text(key),
            subtitle: Text(item),
          );
        } else if (item is Map) {
          if (item.containsKey("isTOS")) {
            return ListTile(
              title: Text(key),
              onTap: _showTOS,
            );
          }

          return ListTile(
            title: Text(key),
            subtitle: Text(item["value"].toString()),
            onTap: () {
              _openUrl(item["url"].toString());
            },
          );
        }

        return Spacer();
      },
    );
  }

  Future<void> setupAppInfo() async {
    var ln10 = context.l10n;
    appData[ln10.lblAppName] = ln10.appBarTitle;
    appData[ln10.lblVersion] = await fetchAppVersion();
    appData[ln10.lblDeveloper] = developerName;
    appData[ln10.lblContact] = <String, String>{
      "value": developerEmail,
      "url": "mailto:$developerEmail",
    };
    appData[ln10.lblWebsite] = <String, String>{
      "value": developerWebsite,
      "url": developerWebsite,
    };
    appData[ln10.lblSourceCode] = <String, String>{
      "value": developerSourceCode,
      "url": developerSourceCode,
    };
    appData[ln10.lblTOS] = <String, String>{"isTOS": "tos"};
    setState(() {});
  }

  Future<String> fetchAppVersion() async {
    final packageInfo = await PackageInfo.fromPlatform();
    return packageInfo.version;
  }

  Future<void> _openUrl(String url) async {
    //TODO: Add email permission to iOS
    // Add any URL schemes passed to canLaunch as LSApplicationQueriesSchemes
    // entries in your Info.plist file.
    // https://pub.dev/packages/url_launcher
    await launchUrl(Uri.parse(url));
  }

  Future<void> _showTOS() async {
    final ln10 = context.l10n;

    final tosDialog = AlertDialog(
      title: Text(ln10.lblTOS),
      content: SingleChildScrollView(
        child: HtmlWidget(
          ln10.tos,
        ),
      ),
      actions: <Widget>[
        TextButton(
          child: Text(ln10.lblOK),
          onPressed: () {
            Navigator.of(context).pop();
          },
        ),
      ],
    );

    await showDialog<void>(
      context: context,
      builder: (ctx) => tosDialog,
      useSafeArea: true,
    );
  }
}
