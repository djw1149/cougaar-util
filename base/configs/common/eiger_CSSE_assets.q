Driver = oracle.jdbc.driver.OracleDriver
Database = jdbc:oracle:thin:@${marine.database}
Username = ${marine.database.plugin.user}
Password = ${marine.database.plugin.password}


# First, get the personnel and generate an aggregate asset
%SQLAggregateAssetCreator
query = select 'Personnel' NSN, SUM(personnel) QTY_OH, 'MilitaryPersonnel' NOMENCLATURE \
	from ue_summary_mtmc \
    	where uic = :uic

%SQLAggregateAssetCreator
query = select NSN, SUM(QTY_OH), 'PALLET, AIRCRAFT 463L' NOMENCLATURE \
	from jtav_equipment \
	where nsn = '1679008204896' and uic = :uic and uln like '%A' \
	group by nsn

# Then, get the Fly-In Element(FIE) and generate an aggregate asset
%SQLAggregateAssetCreator
query = select NSN, SUM(QTY_OH), '' NOMENCLATURE \
	from jtav_equipment \
        where uic = :uic and uln like '%A' and \
        NSN in ( '0000000000000', \
                    '0000000000001',\
                    '0000000000007',\
                    '0000000000055',\
                    '0000000001555',\
                    '000000000AM01',\
                    '000000000DP03',\
                    '000000000E193',\
                    '0000000013456',\
                    '00000000G1603',\
                    '00000000G1723',\
                    '00000000G3112',\
                    '00000000G3187',\
                    '00000000G3300',\
                    '00000000G3313',\
                    '00000000G3316',\
                    '010037CUB4323',\
                    '01226809CUB00',\
                    '100628A',\
                    '1014230948172',\
                    '1055012035883',\
                    '1262AS1001',\
                    '1340012453945',\
                    '1350008959756',\
                    '1378AS100',\
                    '1426AS100',\
                    '1430012861314',\
                    '1440011049834',\
                    '1450010291634',\
                    '1670PALLET001',\
                    '1676AS1001',\
                    '1710010990281',\
                    '1710011002760',\
                    '1710011002761',\
                    '1710011011899',\
                    '1710011599007',\
                    '1730003438033',\
                    '1730004800850',\
                    '1730004947149',\
                    '1730010181524',\
                    '1730011360842',\
                    '1740004147149',\
                    '2310011467194',\
                    '2310013723934',\
                    '2320000508905',\
                    '2320000508987',\
                    '2320000510489',\
                    '2320010478753',\
                    '2320010478755',\
                    '2320010478756',\
                    '2320010478771',\
                    '2320011077155',\
                    '2320011077156',\
                    '2320011467187',\
                    '2320011467188',\
                    '2320011467190',\
                    '2320011467193',\
                    '2320011760467',\
                    '2320011760468',\
                    '2320011760469',\
                    '2320011766928',\
                    '2320011775167',\
                    '2320012052682',\
                    '2320013122616',\
                    '2320013245915',\
                    '2320013297162',\
                    '2320013334129',\
                    '2320013469317',\
                    '2320013719577',\
                    '2320013719578',\
                    '2320013719583',\
                    '2320013723933',\
                    '2320013920293',\
                    '2330001418050',\
                    '2330002214939',\
                    '2330005422039',\
                    '2330005422831',\
                    '2330009337426',\
                    '2330011677262',\
                    '2330013590080',\
                    '2330013725641',\
                    '2330014424975',\
                    '2350008087100',\
                    '2350011112274',\
                    '2410011551588',\
                    '2410012541667',\
                    '2420011602754',\
                    '2590012308862',\
                    '2995001690136',\
                    '3431011539585',\
                    '3510011656845',\
                    '3655002249142',\
                    '3805001402427',\
                    '3805011504795',\
                    '3805011531854',\
                    '3805012400995',\
                    '3805012793635',\
                    '3805013183415',\
                    '3810011650646',\
                    '3810012681737',\
                    '3815012919224',\
                    '3820009508584',\
                    '3830011551587',\
                    '3830011715776',\
                    '3895011353703',\
                    '3895012948235',\
                    '3895014538573',\
                    '3930010823758',\
                    '3930012756420',\
                    '3930013052111',\
                    '3990001417261',\
                    '3990011907165',\
                    '404GE402LLENG',\
                    '4110011079078',\
                    '4120003500184',\
                    '4210012594609',\
                    '4310011240883',\
                    '4320011101993',\
                    '4320011284244',\
                    '4320011582954',\
                    '4320012616470',\
                    '4320013181853',\
                    '4510011636775',\
                    '4610011138651',\
                    '4610011207525',\
                    '4718092374098',\
                    '4910013601584',\
                    '4920009332824',\
                    '4920MFVAN0001',\
                    '4930000179167',\
                    '4930001881701',\
                    '4930010940026',\
                    '4930012086400',\
                    '4930012404579',\
                    '4940010365784',\
                    '4940012355080',\
                    '4940013338470',\
                    '5180010619291',\
                    '5180010619292',\
                    '5411012066037',\
                    '5411012981661',\
                    '5411013046121',\
                    '5411013046122',\
                    '5411013046123',\
                    '5411013057366',\
                    '5411013554323',\
                    '5420001723520',\
                    '5420005229599',\
                    '5420008892020',\
                    '5430011069677',\
                    '5430012039971',\
                    '5430012404578',\
                    '5430013517813',\
                    '5555555555555',\
                    '5805011883993',\
                    '5820010491888',\
                    '5820012347129',\
                    '5820013375294',\
                    '5820013618536',\
                    '5825012986961',\
                    '5825013662452',\
                    '5826012514705',\
                    '5840010205631',\
                    '5840011386490',\
                    '5840011414378',\
                    '5840012001754',\
                    '5840012291276',\
                    '5840012501765',\
                    '5865011883309',\
                    '5865012364235',\
                    '5895010074788',\
                    '5895011106584',\
                    '5895011278134',\
                    '5895011278135',\
                    '5895012200321',\
                    '5895012848305',\
                    '5895012848306',\
                    '5895013333040',\
                    '5895013387024',\
                    '5895013389632',\
                    '5895013547601',\
                    '5895013682214',\
                    '5895013682238',\
                    '5895013790125',\
                    '5895014602551',\
                    '5985011271618',\
                    '5985011795494',\
                    '60DGOV',\
                    '6110012726952',\
                    '6110012726953',\
                    '6110012732387',\
                    '6115000978021',\
                    '6115001113914',\
                    '6115001181240',\
                    '6115001181243',\
                    '6115001181253',\
                    '6115001339101',\
                    '6115004651027',\
                    '6115004651030',\
                    '6115010366374',\
                    '6115010372005',\
                    '6115011504140',\
                    '6115012747389',\
                    '6115012747390',\
                    '6115012747392',\
                    '6115012755061',\
                    '6145013413977',\
                    '6210004073068',\
                    '6230002126378',\
                    '6230002126379',\
                    '6230011701408',\
                    '6350013821826',\
                    '6625004710223',\
                    '6675013842987',\
                    '67A252H1101',\
                    '7010013910168',\
                    '8002062643810',\
                    '8115011994017',\
                    '8115013540797',\
                    '8145001862999',\
                    '8145013163295',\
                    '8340002572557',\
                    'BICW0007',\
                    'BICY0030',\
                    '5820009124544',\
                    '5680004901384',\
                    '4930002329236',\
                    '4120005757200',\
                    '8115013713690',\
                    '8340009516419',\
                    '8340005437788',\
                    '4940000140496',\
                    '2320001779258',\
                    '1025010266648',\
                    '2350010809087',\
                    '2350010818138',\
                    '2350010809088',\
                    '2320011231609',\
                    '2320011231602',\
                    '2320011231612',\
                    '2350001226826',\
                    '4931012732389',\
                    '2350010871095')\
	group by NSN

# Then, get the PREPO Assets and generate an aggregate asset
%SQLAggregateAssetCreator
query = select NSN, SUM(QTY_OH), '' NOMENCLATURE \
	from jtav_equipment \
        where uic = :uic and uln like '%B' and \
        NSN in ( '0000000000000', \
                    '0000000000001',\
                    '0000000000007',\
                    '0000000000055',\
                    '0000000001555',\
                    '000000000AM01',\
                    '000000000DP03',\
                    '000000000E193',\
                    '0000000013456',\
                    '00000000G1603',\
                    '00000000G1723',\
                    '00000000G3112',\
                    '00000000G3187',\
                    '00000000G3300',\
                    '00000000G3313',\
                    '00000000G3316',\
                    '010037CUB4323',\
                    '01226809CUB00',\
                    '100628A',\
                    '1014230948172',\
                    '1055012035883',\
                    '1262AS1001',\
                    '1340012453945',\
                    '1350008959756',\
                    '1378AS100',\
                    '1426AS100',\
                    '1430012861314',\
                    '1440011049834',\
                    '1450010291634',\
                    '1670PALLET001',\
                    '1676AS1001',\
                    '1710010990281',\
                    '1710011002760',\
                    '1710011002761',\
                    '1710011011899',\
                    '1710011599007',\
                    '1730003438033',\
                    '1730004800850',\
                    '1730004947149',\
                    '1730010181524',\
                    '1730011360842',\
                    '1740004147149',\
                    '2310011467194',\
                    '2310013723934',\
                    '2320000508905',\
                    '2320000508987',\
                    '2320000510489',\
                    '2320010478753',\
                    '2320010478755',\
                    '2320010478756',\
                    '2320010478771',\
                    '2320011077155',\
                    '2320011077156',\
                    '2320011467187',\
                    '2320011467188',\
                    '2320011467190',\
                    '2320011467193',\
                    '2320011760467',\
                    '2320011760468',\
                    '2320011760469',\
                    '2320011766928',\
                    '2320011775167',\
                    '2320012052682',\
                    '2320013122616',\
                    '2320013245915',\
                    '2320013297162',\
                    '2320013334129',\
                    '2320013469317',\
                    '2320013719577',\
                    '2320013719578',\
                    '2320013719583',\
                    '2320013723933',\
                    '2320013920293',\
                    '2330001418050',\
                    '2330002214939',\
                    '2330005422039',\
                    '2330005422831',\
                    '2330009337426',\
                    '2330011677262',\
                    '2330013590080',\
                    '2330013725641',\
                    '2330014424975',\
                    '2350008087100',\
                    '2350011112274',\
                    '2410011551588',\
                    '2410012541667',\
                    '2420011602754',\
                    '2590012308862',\
                    '2995001690136',\
                    '3431011539585',\
                    '3510011656845',\
                    '3655002249142',\
                    '3805001402427',\
                    '3805011504795',\
                    '3805011531854',\
                    '3805012400995',\
                    '3805012793635',\
                    '3805013183415',\
                    '3810011650646',\
                    '3810012681737',\
                    '3815012919224',\
                    '3820009508584',\
                    '3830011551587',\
                    '3830011715776',\
                    '3895011353703',\
                    '3895012948235',\
                    '3895014538573',\
                    '3930010823758',\
                    '3930012756420',\
                    '3930013052111',\
                    '3990001417261',\
                    '3990011907165',\
                    '404GE402LLENG',\
                    '4110011079078',\
                    '4120003500184',\
                    '4210012594609',\
                    '4310011240883',\
                    '4320011101993',\
                    '4320011284244',\
                    '4320011582954',\
                    '4320012616470',\
                    '4320013181853',\
                    '4510011636775',\
                    '4610011138651',\
                    '4610011207525',\
                    '4718092374098',\
                    '4910013601584',\
                    '4920009332824',\
                    '4920MFVAN0001',\
                    '4930000179167',\
                    '4930001881701',\
                    '4930010940026',\
                    '4930012086400',\
                    '4930012404579',\
                    '4940010365784',\
                    '4940012355080',\
                    '4940013338470',\
                    '5180010619291',\
                    '5180010619292',\
                    '5411012066037',\
                    '5411012981661',\
                    '5411013046121',\
                    '5411013046122',\
                    '5411013046123',\
                    '5411013057366',\
                    '5411013554323',\
                    '5420001723520',\
                    '5420005229599',\
                    '5420008892020',\
                    '5430011069677',\
                    '5430012039971',\
                    '5430012404578',\
                    '5430013517813',\
                    '5555555555555',\
                    '5805011883993',\
                    '5820010491888',\
                    '5820012347129',\
                    '5820013375294',\
                    '5820013618536',\
                    '5825012986961',\
                    '5825013662452',\
                    '5826012514705',\
                    '5840010205631',\
                    '5840011386490',\
                    '5840011414378',\
                    '5840012001754',\
                    '5840012291276',\
                    '5840012501765',\
                    '5865011883309',\
                    '5865012364235',\
                    '5895010074788',\
                    '5895011106584',\
                    '5895011278134',\
                    '5895011278135',\
                    '5895012200321',\
                    '5895012848305',\
                    '5895012848306',\
                    '5895013333040',\
                    '5895013387024',\
                    '5895013389632',\
                    '5895013547601',\
                    '5895013682214',\
                    '5895013682238',\
                    '5895013790125',\
                    '5895014602551',\
                    '5985011271618',\
                    '5985011795494',\
                    '60DGOV',\
                    '6110012726952',\
                    '6110012726953',\
                    '6110012732387',\
                    '6115000978021',\
                    '6115001113914',\
                    '6115001181240',\
                    '6115001181243',\
                    '6115001181253',\
                    '6115001339101',\
                    '6115004651027',\
                    '6115004651030',\
                    '6115010366374',\
                    '6115010372005',\
                    '6115011504140',\
                    '6115012747389',\
                    '6115012747390',\
                    '6115012747392',\
                    '6115012755061',\
                    '6145013413977',\
                    '6210004073068',\
                    '6230002126378',\
                    '6230002126379',\
                    '6230011701408',\
                    '6350013821826',\
                    '6625004710223',\
                    '6675013842987',\
                    '67A252H1101',\
                    '7010013910168',\
                    '8002062643810',\
                    '8115011994017',\
                    '8115013540797',\
                    '8145001862999',\
                    '8145013163295',\
                    '8340002572557',\
                    'BICW0007',\
                    'BICY0030',\
                    '5820009124544',\
                    '5680004901384',\
                    '4930002329236',\
                    '4120005757200',\
                    '8115013713690',\
                    '8340009516419',\
                    '8340005437788',\
                    '4940000140496',\
                    '2320001779258',\
                    '1025010266648',\
                    '2350010809087',\
                    '2350010818138',\
                    '2350010809088',\
                    '2320011231609',\
                    '2320011231602',\
                    '2320011231612',\
                    '2350001226826',\
                    '4931012732389',\
                    '2350010871095')\
	group by NSN
