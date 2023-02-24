import 'package:banjalukawifi/cubit/network/network_cubit.dart';
import 'package:banjalukawifi/l10n/l10n.dart';
import 'package:banjalukawifi/screens/mobile/app_info_screen.dart';
import 'package:banjalukawifi/screens/mobile/network_form_screen.dart';
import 'package:banjalukawifi/screens/mobile/network_map_screen.dart';
import 'package:banjalukawifi/screens/mobile/networks_screen.dart';
import 'package:banjalukawifi/screens/tablet/app_info_screen.dart' as tablet;
import 'package:banjalukawifi/utility/responsive.dart';
import 'package:banjalukawifi/widgets/ad_banner.dart';
import 'package:banjalukawifi/widgets/bottom_navigation_widget.dart';
import 'package:banjalukawifi/widgets/custom_search_delegate.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:permission_handler/permission_handler.dart';

class App extends StatefulWidget {
  const App({Key? key}) : super(key: key);

  @override
  State<App> createState() => _AppState();
}

class _AppState extends State<App> {
  int _currentPageIndex = 0;
  final List<Widget> _pagesMobile = const [
    NetworkScreen(),
    NetworkFormScreen(),
    NetworkMapScreen(),
    AppInfoScreen(),
  ];

  final List<Widget> _pagesTablet = const [
    NetworkScreen(),
    NetworkFormScreen(),
    tablet.AppInfoScreen(),
  ];

  @override
  void initState() {
    context.read<NetworkCubit>().fetchNetworks();

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
      body: Responsive(
        mobile: Column(
          children: [
            Expanded(
              child: _pagesMobile[_currentPageIndex],
            ),
            AdBanner(isTablet: false),
          ],
        ),
        tablet: Container(
          child: Column(
            children: [
              Expanded(child: _pagesTablet[_currentPageIndex]),
              Expanded(child: const NetworkMapScreen()),
              AdBanner(isTablet: true),
            ],
          ),
        ),
      ),
      bottomNavigationBar: BottomNavigationWidget(
        currentPageIndex: _currentPageIndex,
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
    SystemChrome.setPreferredOrientations([
      DeviceOrientation.landscapeRight,
      DeviceOrientation.landscapeLeft,
      DeviceOrientation.portraitUp,
      DeviceOrientation.portraitDown,
    ]);
    super.dispose();
  }
}
