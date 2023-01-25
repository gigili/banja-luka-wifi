import 'package:banjalukawifi/cubit/network/network_cubit.dart';
import 'package:banjalukawifi/l10n/l10n.dart';
import 'package:banjalukawifi/screens/app_info_screen.dart';
import 'package:banjalukawifi/screens/network_form_screen.dart';
import 'package:banjalukawifi/screens/network_map_screen.dart';
import 'package:banjalukawifi/screens/networks_screen.dart';
import 'package:banjalukawifi/widgets/custom_search_delegate.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';
import 'package:permission_handler/permission_handler.dart';

class App extends StatefulWidget {
  const App({Key? key}) : super(key: key);

  @override
  State<App> createState() => _AppState();
}

class _AppState extends State<App> {
  int _currentPageIndex = 0;
  final List<Widget> _pages = const [
    NetworkScreen(),
    NetworkFormScreen(),
    NetworkMapScreen(),
    AppInfoScreen(),
  ];

  final BannerAd myBanner = BannerAd(
    adUnitId: kDebugMode ? "ca-app-pub-3940256099942544/6300978111" : 'ca-app-pub-7166119252755550/6897013025',
    size: AdSize.banner,
    request: AdRequest(),
    listener: BannerAdListener(),
  );

  @override
  void initState() {
    context.read<NetworkCubit>().fetchNetworks();
    myBanner.load();

    SystemChrome.setPreferredOrientations([
      DeviceOrientation.portraitUp,
    ]);

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      resizeToAvoidBottomInset: false,
      appBar: AppBar(
        title: Text(context.l10n.appBarTitle),
        actions: [
          IconButton(
            onPressed: () {
              // method to show the search bar
              showSearch(
                context: context,
                // delegate to customize the search bar
                delegate: CustomSearchDelegate(),
              );
            },
            icon: const Icon(Icons.search),
          )
        ],
      ),
      body: Column(
        children: [
          Expanded(
            child: _pages[_currentPageIndex],
          ),
          Container(
            alignment: Alignment.center,
            child: AdWidget(ad: myBanner),
            width: myBanner.size.width.toDouble(),
            height: myBanner.size.height.toDouble(),
          ),
        ],
      ),
      bottomNavigationBar: BottomNavigationBar(
        type: BottomNavigationBarType.shifting,
        selectedItemColor: Colors.blue[700],
        selectedFontSize: 12,
        unselectedFontSize: 11,
        showUnselectedLabels: false,
        iconSize: 30,
        currentIndex: _currentPageIndex,
        items: [
          BottomNavigationBarItem(
            label: context.l10n.lblNetworks,
            icon: Icon(Icons.wifi),
          ),
          BottomNavigationBarItem(
            label: context.l10n.lblAddEditNetwork,
            icon: Icon(Icons.edit),
          ),
          BottomNavigationBarItem(
            label: context.l10n.lblNetworksMap,
            icon: Icon(Icons.map),
          ),
          BottomNavigationBarItem(
            label: context.l10n.lblAppInfo,
            icon: Icon(Icons.info_outline),
          ),
        ],
        onTap: (int index) => setState(() {
          _currentPageIndex = index;
        }),
      ),
    );
  }

  @override
  void didChangeDependencies() {
    setPermissions();
    super.didChangeDependencies();
  }

  Future<PermissionStatus> setPermissions() async {
    final locationStatus = await Permission.location.request();

    if (locationStatus.isDenied || locationStatus.isPermanentlyDenied) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(context.l10n.errorNoLocationPermissions)),
      );
    }

    return locationStatus;
  }

  @override
  void dispose() {
    myBanner.dispose();
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.landscapeRight,
      DeviceOrientation.landscapeLeft,
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
    ]);
    super.dispose();
  }
}
