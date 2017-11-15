# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [2.4.0] - UNRELEASED
### Changed
- added border to QuickSearch dialog

## [2.3.0] - 2017-11-05
### Added
- added support to handle snippets in offline mode
- added settings dialog with editor theme and font size option
- added title and description to snippet view
- added log4j.properties configuration
- log error if can't open web browser
- text can be dragged into table to create a new snippet
- snippets can be dragged directly into editor and the code are copied
- added "save as file" feature
- added "clone snippet" feature
- added "snippet properties" dialog
- detect binary file

### Fixed
- show a dialog error if verification code is not correct

## [2.2.0] - 2017-04-22
### Added
- added key shortcut to get focus on filter snippets box
- added welcome panel
- about dialog shows plugin version, framework version and data storage directory

## [2.1.0] - 2017-03-25
### Added
- new CHANGELOG.md file

### Changed
- Storage is based on internal db with [Ormlite](http://ormlite.com/) and [H2](http://www.h2database.com)

### Fixed
- Fixed logout on IDE restart

### Removed
- removed IStorage

## 2.0.0 - 2016-07-11
### Added
- new user interface
- list of snippets stored locally
- syntax highlight powered by <a href="http://bobbylight.github.io/RSyntaxTextArea/">RSyntaxTextArea</a>
- snippets are synchronized with server
- filter snippets
- quick search feature
- insert selected snippet at cursor in document 
 ### Changed
- Error messages with log and dialog

## 1.1.2 - 2014-10-03
### Added
- Console write also snippet title when new snippet is created (thanks to bejoy)

### Changed
- Switched authentication from OAuth 1.0a to OAuth 2

## 1.1.0 - 2014-08-19
### Added
- Added "Search snippets" feature
- Fixed some minor bugs

## 1.0.0 - 2014-04-14
### Added
- First release with feature "Create snippet"

[2.3.0]: https://github.com/massimozappino/tagmycode-java-plugin-framework/compare/v2.2.0...v2.3.0
[2.2.0]: https://github.com/massimozappino/tagmycode-java-plugin-framework/compare/v2.1.0...v2.2.0
[2.1.0]: https://github.com/massimozappino/tagmycode-java-plugin-framework/compare/v2.0.0...v2.1.0
