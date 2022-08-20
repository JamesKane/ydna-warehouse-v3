# YDNA-Warehouse v3

# WARNING - Early Development Project
This repository contains the next generation codebase for YDNA-Warehouse v3.  This release will return the warehouse to
using a NoSQL DB instead of PostgreSQL to leverage keeping matrix rows in a single document.  It will also replace the
current Twirl server-side front-end components with a TBD front-end.

It is also possible that Play will be replaced in the future given the framework was turned over to the community by 
Lightbend.  Emerging options for Scala3 may be a better fit in light of this.

The technology stack ***YDNA-Warehouse v3*** is built with:
1) [Play Framework](https://www.playframework.com/) - Provides a modern high velocity web framework running on JVM
2) [Silhoutte](https://silhouette.readme.io/) - Provides Authentication and Authorization for the **Play Framework**
3) [ReactiveMongo](http://reactivemongo.org/) - Provides an Asynchronous Scala Driver for MongoDB
4) [MongoDB](https://www.mongodb.com/) - Provides a NoSQL database backend
5) [Bootstrap](https://getbootstrap.com) - Provides a responsive layout for the front-end
6) [AWS SDK](https://aws.amazon.com/tools) - Provides APIs for dealing with S3 and other AWS service
7) [HTSJDK](https://github.com/samtools/htsjdk) - Provides APIs for dealing with common sequencing data formats

## Contributing
The ***YDNA-Warehouse*** codebase is public to facilitate development by the user community.  Pull requests
are welcomed but the core team members must review the impacts on security, privacy and site operational budgets.

## License
See [LICENSE](LICENSE) for more information.