<br />
<div align="center">
    <h3 align="center">Reactive File Hosting Rest API</h3>
</div>
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#o-projekcie">O projekcie</a>
      <ul>
        <li><a href="#built-with">Cel</a></li>
      </ul>
      <ul>
        <li><a href="#built-with">Biblioteki/Zależności</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Core aplikacji</a>
      <ul>
        <li><a href="#dostep-do-danych">Dostęp do danych</a></li>
        <li><a href="#warstwa-prezentacji">Warstwa prezentacji</a></li>
      </ul>
    </li>
    <li><a href="#license">Licencja</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## O projekcie
### Cel
Założeniem projektu było stworzenie prostego nieblokującego Rest API do zapisu/odczytu dowolnych plików z wykorzystaniem przestrzeni dyskowej.
Projekt bazuje na JDK 17 LTS i Spring Boot 2.6.1.



### Biblioteki/Zależności

* Webflux
* Project reactor
* Spring Data R2dbc
* H2database
* Lombok



<!-- GETTING STARTED -->
## Core aplikacji
### Dostęp do danych

Encja <b>File</b>

   ```sh
@Table

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(doNotUseGetters = true)
public class File implements Persistable<String> {

    @Id
    # Poza wykorzytaniem jako primary key służy rownież jako nazwa pliku w przestrzeni dyskowej.
    private String id;

    # Oryginalna nazwa zaimportowanego pliku. 
    private String originalFileName;
    
    # Typ MIME odczytany na podstawie oryginalnej nazwy pliku.
    # Użyty do sprecyzowania "Content type" w nagłówku podczas pobrania pliku.  
    private String mediaType;

    @CreatedDate
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    # Utworzona data i czas zapisania encji dzięki Spring Data Auditing
    private LocalDateTime createdDate;

    @Builder
    public File(String originalFileName, MediaType mediaType) {
        # Generowanie ID pliku 
        this.id = FileUuidGenerator.generate(); 
        this.originalFileName = originalFileName;
        this.mediaType = mediaType.getType() + "/" + mediaType.getSubtype();
    }

    @Override
    @JsonIgnore
    # Nadpisanie metody interfejsu Persistable, dzięki której Spring Data rozponaje encję jako nową.
    # Powodem nadpisania jest fakt iż ID encji jest generowane przed utrwaleniem jej w bazie danych.
    public boolean isNew() { 
        return Objects.isNull(createdDate);
    }

    # Mapowanie pola createdDate typu LocalDateTime na ZonedDateTime z domyślną strefą czasową aplikacji 
    public ZonedDateTime getCreatedDate() {
        return Objects.nonNull(createdDate) ? createdDate.atZone(ZoneId.systemDefault()) : null;
    }
}
   ```

Zawartość pliku jest zapisana w przestrzeni dyskowej w katalogu zdefiniowanym w application.properties


### Warstwa prezentacji

<b>
GET /api/files - lista wszystkich dostepnych plików <br/>
GET /api/files/{id} - pobranie zawartości pliku <br/>
POST /api/files - import pliku (z wykorzystaniem FormData) <br/>
DELETE /api/files/{id} - usunięcie pliku <br/>
</b>

Przykładowy POST response
```

{
    "id": "1639931394448_05527ff6e12840648f45eacee866f114", # id pliku
    "originalFileName": "dr2.PNG", # oryginalna nazwa pliku
    "mediaType": "image/png", # typ MIME 
    "createdDate": "2021-12-19T17:29:54.4991365+01:00" # data i czas importu
}
```

<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE.txt` for more information.
