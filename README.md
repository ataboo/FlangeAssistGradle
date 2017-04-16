Boilermaker Toolbox
===================
Boilermaker Toolbox is a FREE reference tool designed to help Boilermakers in Canada.  This tool is still early in development so more features will be added in the future.

Tell your friends with iPhones to search for BoilerMac Toolbox in the IOS Store.

###Main features so far (v0.30):
- NEW Rope Value Table
- BMToolbox Reporter Launcher (Apprentice Reports)
- Gross Tax Calculator
- Rigging Calculator: finds load on slings, nylon sling table.
- Shape Calculator: finds volume/surface area.
- Welding Electrode Reference: gives information based on AWS code.
- Cash Counter: estimates your cash as you earn it
- Unit Converter (length, pressure, volume, mass)
- Paycheque Calculator (AB, ON, BC Maintenance agreements)
- ASME flange tool (provides details based on flange size and rating)
- Torque pattern generator
- CPI/Oil price wage increase estimator
- List of links to Canadian Boilermaker halls.

###Features to come:
- Component weight estimator
- More general trade reference material
- Larger flange sizes in table

Feel free to send feedback or suggestions to bmtoolbox@gmail.com

###Changes:

####V0.41 (04/16/17):
- NEW: Callout Viewer
- Updated wages/taxes for 2017.
- Updated 146 overtime changes.
- Re-wrote pay calculator tax calculating and config parsing.
- Tweaked some spinners misbehaving in Nougat.
- Fixed side-panel not retaining last selected tool on reopen.

####V0.30 (05/17/16):
- Updated wages for ON, MB, SK, BC
- Re-wrote Cash Counter with new animations
- Re-worked settings menu

####V0.29.4 (12/31/15):
- Fixed crash caused by Custom Day settings error.

####V0.29.3 (12/26/15):
- Tax and Wage values updated
- Added SK and NF to Pay Calculator
- Provinces now have Night Shift Premium, Dues, and Vacation rates as available through BCA
- Provinces have 1.5 time on night shift as per agreements
- Provinces have overtime at double rate as per agreements
- Fixed BC and ON taxes, CB wages
- TaxManager and PayCalc overhauled...again.
- Taxes now properly calculated with Union Dues credited
- Days of the week spinners now resize properly to screen
- Icon changed to new IOS BoilerMac Toolbox icon.
- Menu doesn't open on first run
- Added 2016 tax, updated rates.

####V0.28 (11/04/15):
- Updated wage rates
- Fixed vacation rates
- Added custom vacation rate setting
- Converted backend to read tax and wage values off of CSVs

####V0.27 (04/03/15):
- Updated wage rates
- Started converting tax values to json

####V0.26b (04/03/15):
- Added NS, NB, PEI tax rates
- Added wages for each and Cape Breton
- Fixed vacation rate rounding

####V0.26a beta (03/09/15):
- Custom Days rounding fix

####V0.25 (03/05/15):
- Fixed crash caused by bad handling of invalid custom day setting
- Fixed custom wage
- Reorganized settings menu
- Converted most tax calculation to BigDecimal
- Fixed tax credit for wages that wouldn't max CPP/EI

####V0.24 (02/19/15):
- Fixed (hopefully) crash in Paycheque Calculator
- PayCalc will now wipe settings if province is invalid

####V0.23 (02/19/15):

- Added BMToolbox Reporter launcher
- Generate Apprentice Report PDFs
- Added Manitoba wages and taxes
- Added Gross Tax Calculator

####V0.22 (02/11/15):
- Changed Menu layout and theme
- Paycheque Calculator remembers values between uses
- Migrated to Gradle
- Cleaned up Hall Links
- Updated 146 monthly due rate

####V0.21 (09/12/14):
- Added flange table by stud size
- Added 2015 tax rates (AB, BC, ON)

####V0.20 (14/11/14):
- Added Rigging Calculator
- Added rate indicator, weekend double toggle on Cash Counter
- Fixed B7/B7M Torques being switched in Flange Table
- Added Larger ASME Flanges
- Updated AB Wage Rates

####v0.19 (08/10/14):
- Added Geometric Shape Calculator on unit converter
- Added Welding Electrode reference
- Added Cash Counter
- Separated tools in new menu layout
- Added fraction to nearest 16th on calculated units
- Remembered to update the About Page

####v0.18 (05/29/14):
-Added basic unit converter (Length, Pressure, Mass, Volume)
-Re-wrote flange calc backend to parse shared JSON file

####v0.17 (05/03/14):
- Added tool to estimate wage increase based on Consumer Price Index and price of oil
Estimate is rough as it's relies on the average wage package of all group 4 trades.
- Updated wages to May 2014 rate for AB, BC, and ON
- Fixed a few more values in the ASME Flange Calculator

####v0.16 (02/08/14):
- Added BC and Ontario tax rates (switch provinces in preferences)
- Added BC and Ontario default wages and vacation rates
- Added Vacation rate box
- Seperated tax calculator backend
- Added custom meal bonus rate with taxable option (lame, I know)
- Added monthly dues and custom rate
- Working dues rate is changeable
- Added alternate "reversing" 8-point torque pattern

####v0.15 (01\28\14):
- Resized day select boxes to fit better on some Androids (Galaxy Note)
- Some flange values were out to lunch
- Added custom LOA rate in preferences
- Cleaned up some code
- Changed validation and "toasted" of custom values

####v0.14:
- Added AB 2014 tax rate
- Added setting in menu to switch tax year
- Fixed tax rate for CPP/EI exempt
- Fixed B7/B7M torques reversed in Flange Reference

####v0.13:
- Fixed CPP deduction rate
- Added CPP, EI, Income Tax, Dues check-boxes
- Added settings menu that allows extra tax deduction	
- Custom wage rate
- Custom daily hours
- Custom travel weekly and daily
- Added North and South (N/S) Alberta rates
- Added daily travel toggle

####v0.12:
- Converted everything to tab layout
- Added "Hall Links" list of Canadian Boilermaker contact info
- Added icons for hall links
- Added stat holidays (right on time) to paycheque calculator

####v0.11:
- Added torque pattern generator
- Changed icon
- Added torque values to Flange Tables

####v0.1:
- Initial public release
- Updated  wages to Nov 3 /13 rate
