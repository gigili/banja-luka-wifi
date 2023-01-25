import 'dart:io';

import 'package:banjalukawifi/app.dart';
import 'package:banjalukawifi/cubit/network/form/network_form_cubit.dart';
import 'package:banjalukawifi/cubit/network/network_cubit.dart';
import 'package:banjalukawifi/l10n/l10n.dart';
import 'package:banjalukawifi/models/network_model.dart';
import 'package:banjalukawifi/providers/api_provider.dart';
import 'package:banjalukawifi/providers/box_provider.dart';
import 'package:banjalukawifi/providers/connectivity_provider.dart';
import 'package:banjalukawifi/providers/network_provider.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:path_provider/path_provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  MobileAds.instance.initialize();

  Directory directory = await getApplicationDocumentsDirectory();

  await Hive.initFlutter(directory.path);
  Hive.registerAdapter(NetworkModelAdapter());
  var box = await Hive.openBox<NetworkModel>("networks");

  SharedPreferences preferences = await SharedPreferences.getInstance();
  runApp(MyApp(box: box, preferences: preferences));
}

class MyApp extends StatelessWidget {
  final Box<NetworkModel> box;
  final SharedPreferences preferences;

  const MyApp({super.key, required this.box, required this.preferences});

  @override
  Widget build(BuildContext context) {
    return MultiRepositoryProvider(
      providers: [
        RepositoryProvider(create: (_) => ConnectivityProvider(Connectivity())),
        RepositoryProvider(create: (_) => ApiProvider()),
        RepositoryProvider(create: (_) => BoxProvider(box)),
        RepositoryProvider(
          create: (ctx) => NetworkProvider(
            apiProvider: ctx.read<ApiProvider>(),
            boxProvider: ctx.read<BoxProvider>(),
            connectivityProvider: ctx.read<ConnectivityProvider>(),
          ),
        )
      ],
      child: MultiBlocProvider(
        providers: [
          BlocProvider(create: (ctx) => NetworkCubit(ctx.read<NetworkProvider>())),
          BlocProvider(create: (ctx) => NetworkFormCubit(ctx.read<NetworkProvider>())),
        ],
        child: MaterialApp(
          debugShowCheckedModeBanner: kDebugMode,
          title: 'Banja Luka WiFi',
          theme: ThemeData.dark(useMaterial3: true),
          localizationsDelegates: AppLocalizations.localizationsDelegates,
          supportedLocales: AppLocalizations.supportedLocales,
          home: const App(),
        ),
      ),
    );
  }
}
